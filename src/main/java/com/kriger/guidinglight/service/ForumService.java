package com.kriger.guidinglight.service;

import com.kriger.guidinglight.model.User;
import com.kriger.guidinglight.model.forum.Answer;
import com.kriger.guidinglight.model.forum.Comment;
import com.kriger.guidinglight.model.forum.Question;
import com.kriger.guidinglight.model.forum.Tag;
import com.kriger.guidinglight.model.projection.QuestionDetail;
import com.kriger.guidinglight.model.projection.QuestionToTheForum;
import com.kriger.guidinglight.repository.UserRepository;
import com.kriger.guidinglight.repository.forum.AnswerRepository;
import com.kriger.guidinglight.repository.forum.CommentRepository;
import com.kriger.guidinglight.repository.forum.QuestionRepository;
import com.kriger.guidinglight.repository.forum.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ForumService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private List<QuestionToTheForum> questions = new ArrayList<>();

    public void buildQuestions() {
        List<Question> questionRepositoryAll = questionRepository.findAll();

        if (questions.isEmpty()) {
            IntStream.range(0, questionRepositoryAll.size()).forEach(q ->
                    questions.add(
                            QuestionToTheForum.builder()
                                    .id(questionRepositoryAll.get(q).getId())
                                    .title(questionRepositoryAll.get(q).getTitle())
                                    .content(questionRepositoryAll.get(q).getContent())
                                    .answerSize(questionRepositoryAll.get(q).getAnswers().size())
                                    .submissionTime(questionRepositoryAll.get(q).getSubmissionTime())
                                    .build()));
            questions.sort(Comparator.comparing(QuestionToTheForum::getSubmissionTime).reversed());
        }
    }

    public Page<QuestionToTheForum> findPagination(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<QuestionToTheForum> list;

        if (questions.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, questions.size());
            list = questions.subList(startItem, toIndex);
        }

        Page<QuestionToTheForum> questionPage = new PageImpl<>(
                list, PageRequest.of(currentPage, pageSize), questions.size());

        return questionPage;
    }

    public void saveQuestion(Question question) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth.isAuthenticated();
        if (authenticated) {
            String loggedUserEmail = auth.getName();
            User user = userRepository.findByEmail(loggedUserEmail);
            question.setUser(user);
            question.setSubmissionTime(LocalDateTime.now());
            questionRepository.save(question);

            questions.add(0,
                    QuestionToTheForum.builder()
                            .id(question.getId())
                            .title(question.getTitle())
                            .content(question.getContent())
                            .answerSize(question.getAnswers().size())
                            .submissionTime(question.getSubmissionTime())
                            .build());
        }
    }

    public QuestionDetail buildQuestionDetail(Long id) {
        Optional<Question> questionOptional = questionRepository.findById(id);

        if (questionOptional.isPresent()) {
            return null;
        } else {
            Question question = questionOptional.get();

            List<Answer> answers = answerRepository.findAllAnswersByQuestion(question);
            List<Tag> tags = tagRepository.findAllTagsByQuestion(question);
            List<Comment> comments = commentRepository.findAllCommentsByQuestion(question);

            QuestionDetail questionDetail = QuestionDetail.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .submissionTime(question.getSubmissionTime())
                    .tags(tags)
                    .comments(comments)
                    .answers(answers)
                    .build();

            return questionDetail;
        }
    }

}
