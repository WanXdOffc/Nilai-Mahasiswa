package nilaimahasiswa.model;

public class KRS {
    // Sesuai diagram dosen
    private Course  course;
    private double  score;
    private String  grade;

    // Tambahan untuk DB
    private int     id;
    private String  nim;
    private int     semester;
    private String  tahunAjaran;
    private double  nilaiSikap;
    private double  nilaiUTS;
    private double  nilaiUAS;
    private String  nidnDosen; // tambahan

    public KRS(int id, String nim, Course course,
               double nilaiSikap, double nilaiUTS, double nilaiUAS,
               int semester, String tahunAjaran, String nidnDosen) {
        this.id          = id;
        this.nim         = nim;
        this.course      = course;
        this.nilaiSikap  = nilaiSikap;
        this.nilaiUTS    = nilaiUTS;
        this.nilaiUAS    = nilaiUAS;
        this.score       = hitungScore(nilaiSikap, nilaiUTS, nilaiUAS);
        this.grade       = hitungGrade(this.score);
        this.semester    = semester;
        this.tahunAjaran = tahunAjaran;
        this.nidnDosen   = nidnDosen;
    }

    public static double hitungScore(double sikap, double uts, double uas) {
        return (sikap * 0.3) + (uts * 0.35) + (uas * 0.35);
    }

    public static String hitungGrade(double score) {
        if (score >= 85)      return "A";
        else if (score > 75)  return "B";
        else if (score >= 60) return "C";
        else                  return "D";
    }

    // Sesuai diagram dosen
    public String setGrade()       { return hitungGrade(this.score); }
    public Course getCourse()      { return course; }
    public double getScore()       { return score; }
    public String getGrade()       { return grade; }

    // Getter tambahan
    public int    getId()          { return id; }
    public String getNim()         { return nim; }
    public int    getSemester()    { return semester; }
    public String getTahunAjaran() { return tahunAjaran; }
    public double getNilaiSikap()  { return nilaiSikap; }
    public double getNilaiUTS()    { return nilaiUTS; }
    public double getNilaiUAS()    { return nilaiUAS; }
    public String getNidnDosen()   { return nidnDosen; }
}