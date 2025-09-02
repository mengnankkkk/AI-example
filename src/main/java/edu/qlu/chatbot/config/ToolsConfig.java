package edu.qlu.chatbot.config;

import edu.qlu.chatbot.service.AcademicToolsService;
import edu.qlu.chatbot.service.AcademicToolsService.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * 工具配置类
 * 
 * 将教务服务的方法注册为Spring AI可调用的工具函数
 * AI模型可以根据用户问题自动选择和调用这些工具
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Configuration
public class ToolsConfig {

    private final AcademicToolsService academicToolsService;

    public ToolsConfig(AcademicToolsService academicToolsService) {
        this.academicToolsService = academicToolsService;
    }

    /**
     * 课程信息查询工具
     * 
     * @return 课程查询函数
     */
    @Bean
    @Description("查询齐鲁工业大学的课程信息，包括课程名称、授课教师、上课时间、地点和选课情况。" +
                "当用户询问课程相关信息、选课、课程安排等问题时使用此工具。")
    public Function<CourseQuery, CourseInfo> getCourseInfo() {
        return academicToolsService::getCourseInfo;
    }

    /**
     * 成绩查询工具
     * 
     * @return 成绩查询函数
     */
    @Bean
    @Description("查询学生的成绩信息，包括各科成绩、学分和GPA。" +
                "当用户询问成绩、GPA、学分等相关问题时使用此工具。" +
                "需要提供学号和学期信息。")
    public Function<GradeQuery, GradeInfo> getGradeInfo() {
        return academicToolsService::getGradeInfo;
    }

    /**
     * 考试安排查询工具
     * 
     * @return 考试安排查询函数
     */
    @Bean
    @Description("查询考试安排信息，包括考试时间、地点、座位号等。" +
                "当用户询问考试时间、考试地点、考试安排等问题时使用此工具。")
    public Function<ExamQuery, ExamSchedule> getExamSchedule() {
        return academicToolsService::getExamSchedule;
    }

    /**
     * 图书馆信息查询工具
     * 
     * @return 图书馆信息查询函数
     */
    @Bean
    @Description("查询图书馆的相关信息，包括开放时间、座位情况、图书检索等。" +
                "当用户询问图书馆开放时间、座位、借书、还书、图书查找等问题时使用此工具。")
    public Function<LibraryQuery, LibraryInfo> getLibraryInfo() {
        return academicToolsService::getLibraryInfo;
    }

    /**
     * 校历查询工具
     * 
     * @return 校历查询函数
     */
    @Bean
    @Description("查询学校的校历信息，包括学期安排、重要日期、假期安排等。" +
                "当用户询问开学时间、放假时间、考试时间、校历安排等问题时使用此工具。")
    public Function<CalendarQuery, AcademicCalendar> getAcademicCalendar() {
        return academicToolsService::getAcademicCalendar;
    }
}
