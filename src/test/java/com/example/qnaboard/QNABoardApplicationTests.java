package com.example.qnaboard;

import com.example.qnaboard.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QNABoardApplicationTests {
	@Autowired
	private QuestionService questionService;

	@Test
	void testJpa() {
		for(int i = 1; i <= 300; i++){
			String title = String.format("제목 %d", i);
			String content = String.format("내용 %d", i);
			questionService.create(title, content);
		}
	}
}
