package nilaimahasiswa.model;


public class KRS {
    private Course course;
    private double score;
    private String grade;

    private int id;
    private String nim;
    private int semester;
    private String tahunAjaran;

    public KRS(int id, String nim, Course course,
               double score, int semester, String tahunAjaran) {
        this.id = id;
        this.nim = nim;
        this.course = course;
        this.score = score;
        this.grade = hitungGrade(score);
        this.semester = semester;
        this.tahunAjaran = tahunAjaran;
    }

    public Course getCourse() {
        return course;
    }
    public double getScore() {
        return score;
    }
    public String getGrade() {
        return grade;
    }
    
    public int getId() {
        return id;
    }
    public String getNim() {
        return nim;
    }
    public int getSemester() {
        return semester;
    }
    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public static String hitungGrade(double score) {
        if (score >= 85) return "A";
        else if (score > 75) return "B";
        else if (score >= 60) return "C";
        else return "D";
    }

    public String setGrade() {
        return hitungGrade(this.score);
    }

    @Override
    public String toString() {
        return course.getCourseName() + " | " + grade;
    }
}