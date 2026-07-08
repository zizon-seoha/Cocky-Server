package com.cocky.cockyserver.domain.submission.service;

import com.cocky.cockyserver.domain.problem.entity.Difficulty;
import com.cocky.cockyserver.domain.problem.entity.Problem;
import com.cocky.cockyserver.domain.problem.entity.TestCase;
import com.cocky.cockyserver.domain.problem.exception.ProblemNotFoundException;
import com.cocky.cockyserver.domain.problem.repository.ProblemRepository;
import com.cocky.cockyserver.domain.problem.repository.TestCaseRepository;
import com.cocky.cockyserver.domain.round.entity.Round;
import com.cocky.cockyserver.domain.submission.dto.SubmissionRequest;
import com.cocky.cockyserver.domain.submission.dto.SubmissionResponse;
import com.cocky.cockyserver.domain.submission.entity.Submission;
import com.cocky.cockyserver.domain.submission.entity.Verdict;
import com.cocky.cockyserver.domain.submission.exception.LanguageMismatchException;
import com.cocky.cockyserver.domain.submission.exception.RoundClosedException;
import com.cocky.cockyserver.domain.submission.exception.TestCaseNotConfiguredException;
import com.cocky.cockyserver.domain.submission.judge.JudgeRequest;
import com.cocky.cockyserver.domain.submission.judge.JudgeResult;
import com.cocky.cockyserver.domain.submission.judge.JudgeService;
import com.cocky.cockyserver.domain.submission.judge.TestCaseIO;
import com.cocky.cockyserver.domain.submission.repository.SubmissionRepository;
import com.cocky.cockyserver.domain.user.entity.User;
import com.cocky.cockyserver.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Judge0 동기 채점(wait=true)은 초 단위로 걸릴 수 있어 DB 트랜잭션 안에 두지 않는다 —
 * 커넥션을 물고 채점을 기다리면 마감 직전 동시 제출이 몰릴 때 커넥션 풀이 고갈된다.
 * 그래서 검증/조회(짧은 읽기 트랜잭션) → Judge0 호출(트랜잭션 밖) → is_latest 갱신·저장
 * (짧은 쓰기 트랜잭션) 순으로 쪼갠다.
 *
 * <p>같은 클래스의 메서드를 {@code @Transactional}로만 나누면 self-invocation 때문에
 * 프록시가 트랜잭션을 적용하지 못하므로, {@link TransactionTemplate}으로 명시적으로 경계를
 * 긋는다.
 */
@Service
public class SubmissionService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final JudgeService judgeService;
    private final Clock clock;
    private final TransactionTemplate readOnlyTransaction;
    private final TransactionTemplate writeTransaction;

    public SubmissionService(ProblemRepository problemRepository, TestCaseRepository testCaseRepository,
                             SubmissionRepository submissionRepository, UserRepository userRepository,
                             JudgeService judgeService, Clock clock, PlatformTransactionManager transactionManager) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.judgeService = judgeService;
        this.clock = clock;
        this.readOnlyTransaction = new TransactionTemplate(transactionManager);
        this.readOnlyTransaction.setReadOnly(true);
        this.writeTransaction = new TransactionTemplate(transactionManager);
    }

    public SubmissionResponse submit(Long userId, SubmissionRequest request) {
        Validated validated = readOnlyTransaction.execute(status -> validate(request));
        JudgeResult judgeResult = judgeService.judge(validated.judgeRequest());
        return writeTransaction.execute(status -> persist(userId, request, validated, judgeResult));
    }

    /** 문제 조회·검증(404/400/403)과 테스트케이스 로딩까지만 담당 — 짧은 읽기 트랜잭션. */
    private Validated validate(SubmissionRequest request) {
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new ProblemNotFoundException(
                        "존재하지 않는 문제입니다. problemId=" + request.problemId()));

        if (request.language() != problem.getLanguage()) {
            throw new LanguageMismatchException(
                    "문제 언어(%s)와 제출 언어(%s)가 다릅니다.".formatted(problem.getLanguage(), request.language()));
        }

        Round round = problem.getRound();
        if (!round.isActive() || LocalDateTime.now(clock).isAfter(round.getCloseAt())) {
            throw new RoundClosedException("마감된 회차의 문제입니다. roundId=" + round.getId());
        }

        List<TestCaseIO> cases = testCaseRepository.findByProblemIdOrderByIdAsc(problem.getId()).stream()
                .map(this::toTestCaseIO)
                .toList();
        if (cases.isEmpty()) {
            throw new TestCaseNotConfiguredException("문제에 테스트케이스가 없습니다. problemId=" + problem.getId());
        }
        JudgeRequest judgeRequest = new JudgeRequest(request.language(), request.code(), cases);
        return new Validated(problem.getId(), problem.getDifficulty(), judgeRequest);
    }

    /** 채점 결과를 받은 뒤 점수 산정·is_latest 갱신·저장까지 — 짧은 쓰기 트랜잭션. */
    private SubmissionResponse persist(Long userId, SubmissionRequest request, Validated validated,
                                       JudgeResult judgeResult) {
        BigDecimal score = judgeResult.verdict() == Verdict.AC
                ? validated.difficulty().baseScore()
                : BigDecimal.ZERO.setScale(2);

        // 벌크 UPDATE라 영속성 컨텍스트를 거치지 않고 DB에 바로 반영된다. 이 트랜잭션
        // 안에서 기존 Submission을 다시 읽지 않고 새 엔티티만 insert하므로
        // clearAutomatically/flushAutomatically 없이도 순서가 어긋나지 않는다.
        submissionRepository.markPreviousNotLatest(userId, validated.problemId());

        User user = userRepository.getReferenceById(userId);
        Problem problem = problemRepository.getReferenceById(validated.problemId());
        Submission submission = new Submission(user, problem, request.language(), request.code());
        submission.updateResult(judgeResult.verdict(), score);
        submissionRepository.save(submission);

        return new SubmissionResponse(
                submission.getId(), submission.getVerdict(), submission.getScore(),
                judgeResult.passedCount(), judgeResult.totalCount());
    }

    private TestCaseIO toTestCaseIO(TestCase testCase) {
        return new TestCaseIO(testCase.getInput(), testCase.getExpectedOutput());
    }

    private record Validated(Long problemId, Difficulty difficulty, JudgeRequest judgeRequest) {
    }
}
