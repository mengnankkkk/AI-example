package edu.qlu.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 教务工具服务类
 * 
 * 提供与教务系统交互的工具函数，供AI模型调用
 * 这些函数将被注册为Spring AI的Tools，AI可以根据用户问题自动调用
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@Service
public class AcademicToolsService {

    private static final Logger logger = LoggerFactory.getLogger(AcademicToolsService.class);

    /**
     * 查询当前学期课程信息
     * 
     * 这是一个示例工具函数，在实际部署时需要连接真实的教务系统API
     * 
     * @param courseQuery 课程查询条件
     * @return 课程信息
     */
    public CourseInfo getCourseInfo(CourseQuery courseQuery) {
        logger.info("查询课程信息: {}", courseQuery);
        
        // 模拟查询结果（实际应该调用教务系统API）
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setCourseName("示例课程 - " + courseQuery.getCourseName());
        courseInfo.setTeacher("示例教师");
        courseInfo.setSchedule("周一 1-2节");
        courseInfo.setLocation("教学楼A101");
        courseInfo.setAvailableSeats(25);
        courseInfo.setTotalSeats(50);
        courseInfo.setRegistrationDeadline("2024-03-15");
        
        return courseInfo;
    }

    /**
     * 查询学生成绩信息
     * 
     * @param gradeQuery 成绩查询条件
     * @return 成绩信息
     */
    public GradeInfo getGradeInfo(GradeQuery gradeQuery) {
        logger.info("查询成绩信息: {}", gradeQuery);
        
        // 模拟查询结果
        GradeInfo gradeInfo = new GradeInfo();
        gradeInfo.setStudentId(gradeQuery.getStudentId());
        gradeInfo.setSemester(gradeQuery.getSemester());
        gradeInfo.setGrades(List.of(
            Map.of("courseName", "高等数学", "grade", "85", "credits", "4"),
            Map.of("courseName", "英语", "grade", "90", "credits", "3"),
            Map.of("courseName", "计算机基础", "grade", "88", "credits", "3")
        ));
        gradeInfo.setGpa("3.65");
        
        return gradeInfo;
    }

    /**
     * 查询考试安排
     * 
     * @param examQuery 考试查询条件
     * @return 考试安排信息
     */
    public ExamSchedule getExamSchedule(ExamQuery examQuery) {
        logger.info("查询考试安排: {}", examQuery);
        
        // 模拟查询结果
        ExamSchedule schedule = new ExamSchedule();
        schedule.setSemester(examQuery.getSemester());
        schedule.setExams(List.of(
            Map.of(
                "courseName", "高等数学",
                "examDate", "2024-01-15",
                "examTime", "09:00-11:00",
                "location", "教学楼B201",
                "seatNumber", "25"
            ),
            Map.of(
                "courseName", "英语",
                "examDate", "2024-01-17",
                "examTime", "14:00-16:00",
                "location", "教学楼C301",
                "seatNumber", "18"
            )
        ));
        
        return schedule;
    }

    /**
     * 查询图书馆信息
     * 
     * @param libraryQuery 图书馆查询条件
     * @return 图书馆信息
     */
    public LibraryInfo getLibraryInfo(LibraryQuery libraryQuery) {
        logger.info("查询图书馆信息: {}", libraryQuery);
        
        LibraryInfo libraryInfo = new LibraryInfo();
        libraryInfo.setIsOpen(true);
        libraryInfo.setOpeningHours("08:00-22:00");
        libraryInfo.setCurrentTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        libraryInfo.setAvailableSeats(150);
        libraryInfo.setTotalSeats(300);
        
        if (libraryQuery.getBookTitle() != null) {
            libraryInfo.setBookSearchResult("找到相关图书3本，其中2本可借，1本在馆阅览");
            libraryInfo.setBookLocation("图书馆二楼A区");
        }
        
        return libraryInfo;
    }

    /**
     * 查询校历信息
     * 
     * @param calendarQuery 校历查询条件
     * @return 校历信息
     */
    public AcademicCalendar getAcademicCalendar(CalendarQuery calendarQuery) {
        logger.info("查询校历信息: {}", calendarQuery);
        
        AcademicCalendar calendar = new AcademicCalendar();
        calendar.setCurrentSemester("2023-2024学年第二学期");
        calendar.setUpcomingEvents(List.of(
            Map.of("date", "2024-03-01", "event", "开学注册"),
            Map.of("date", "2024-03-15", "event", "选课截止"),
            Map.of("date", "2024-04-15", "event", "期中考试"),
            Map.of("date", "2024-06-20", "event", "期末考试")
        ));
        
        return calendar;
    }

    // ================== 数据传输对象 ==================

    /**
     * 课程查询条件
     */
    public static class CourseQuery {
        private String courseName;
        private String semester;
        private String department;

        // Getters and Setters
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        @Override
        public String toString() {
            return "CourseQuery{courseName='" + courseName + "', semester='" + semester + "', department='" + department + "'}";
        }
    }

    /**
     * 课程信息
     */
    public static class CourseInfo {
        private String courseName;
        private String teacher;
        private String schedule;
        private String location;
        private int availableSeats;
        private int totalSeats;
        private String registrationDeadline;

        // Getters and Setters
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getTeacher() { return teacher; }
        public void setTeacher(String teacher) { this.teacher = teacher; }
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
        public String getRegistrationDeadline() { return registrationDeadline; }
        public void setRegistrationDeadline(String registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    }

    /**
     * 成绩查询条件
     */
    public static class GradeQuery {
        private String studentId;
        private String semester;

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }

        @Override
        public String toString() {
            return "GradeQuery{studentId='" + studentId + "', semester='" + semester + "'}";
        }
    }

    /**
     * 成绩信息
     */
    public static class GradeInfo {
        private String studentId;
        private String semester;
        private List<Map<String, String>> grades;
        private String gpa;

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        public List<Map<String, String>> getGrades() { return grades; }
        public void setGrades(List<Map<String, String>> grades) { this.grades = grades; }
        public String getGpa() { return gpa; }
        public void setGpa(String gpa) { this.gpa = gpa; }
    }

    /**
     * 考试查询条件
     */
    public static class ExamQuery {
        private String semester;
        private String courseName;

        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        @Override
        public String toString() {
            return "ExamQuery{semester='" + semester + "', courseName='" + courseName + "'}";
        }
    }

    /**
     * 考试安排
     */
    public static class ExamSchedule {
        private String semester;
        private List<Map<String, String>> exams;

        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        public List<Map<String, String>> getExams() { return exams; }
        public void setExams(List<Map<String, String>> exams) { this.exams = exams; }
    }

    /**
     * 图书馆查询条件
     */
    public static class LibraryQuery {
        private String bookTitle;
        private String author;

        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        @Override
        public String toString() {
            return "LibraryQuery{bookTitle='" + bookTitle + "', author='" + author + "'}";
        }
    }

    /**
     * 图书馆信息
     */
    public static class LibraryInfo {
        private boolean isOpen;
        private String openingHours;
        private String currentTime;
        private int availableSeats;
        private int totalSeats;
        private String bookSearchResult;
        private String bookLocation;

        public boolean isOpen() { return isOpen; }
        public void setIsOpen(boolean isOpen) { this.isOpen = isOpen; }
        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
        public String getCurrentTime() { return currentTime; }
        public void setCurrentTime(String currentTime) { this.currentTime = currentTime; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
        public String getBookSearchResult() { return bookSearchResult; }
        public void setBookSearchResult(String bookSearchResult) { this.bookSearchResult = bookSearchResult; }
        public String getBookLocation() { return bookLocation; }
        public void setBookLocation(String bookLocation) { this.bookLocation = bookLocation; }
    }

    /**
     * 校历查询条件
     */
    public static class CalendarQuery {
        private String year;
        private String semester;

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
    }

    /**
     * 校历信息
     */
    public static class AcademicCalendar {
        private String currentSemester;
        private List<Map<String, String>> upcomingEvents;

        public String getCurrentSemester() { return currentSemester; }
        public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
        public List<Map<String, String>> getUpcomingEvents() { return upcomingEvents; }
        public void setUpcomingEvents(List<Map<String, String>> upcomingEvents) { this.upcomingEvents = upcomingEvents; }
    }
}
