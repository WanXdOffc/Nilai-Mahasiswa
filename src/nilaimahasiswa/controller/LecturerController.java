package nilaimahasiswa.controller;

import java.sql.SQLException;
import java.util.List;
import nilaimahasiswa.dao.LecturerDAO;
import nilaimahasiswa.model.Lecturer;

public class LecturerController {

    private final LecturerDAO dao;
    public static int DATA_PER_HALAMAN = 7;

    public LecturerController() throws SQLException {
        this.dao = new LecturerDAO();
    }

    public void tambah(String nidn, String nama,
                       String keahlian, String noHp) throws Exception {
        validasi(nidn, nama, keahlian, noHp);
        if (dao.isNidnExists(nidn)) {
            throw new Exception("NIDN " + nidn + " sudah terdaftar!");
        }
        dao.insert(new Lecturer(nidn, nama, nidn, keahlian, noHp));
    }

    public void update(String nidn, String nama,
                       String keahlian, String noHp) throws Exception {
        validasi(nidn, nama, keahlian, noHp);
        dao.update(new Lecturer(nidn, nama, nidn, keahlian, noHp));
    }

    public void hapus(String nidn) throws Exception {
        if (nidn == null || nidn.trim().isEmpty()) {
            throw new Exception("Pilih dosen yang akan dihapus!");
        }
        dao.delete(nidn);
    }

    public List<Lecturer> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return dao.findAll();
        }
        return dao.findByKeyword(keyword.trim());
    }

    public List<Lecturer> getHalaman(int halaman) throws SQLException {
        return dao.findPaged(halaman, DATA_PER_HALAMAN);
    }

    public int getTotalHalaman() throws SQLException {
        return (int) Math.ceil((double) dao.count() / DATA_PER_HALAMAN);
    }

    public int getTotalData() throws SQLException {
        return dao.count();
    }

    private void validasi(String nidn, String nama,
                          String keahlian, String noHp) throws Exception {
        if (nidn.trim().isEmpty())
            throw new Exception("NIDN tidak boleh kosong!");
        if (nama.trim().isEmpty())
            throw new Exception("Nama tidak boleh kosong!");
        if (keahlian.trim().isEmpty())
            throw new Exception("Keahlian tidak boleh kosong!");
        if (noHp.trim().isEmpty())
            throw new Exception("No HP tidak boleh kosong!");
        if (!noHp.trim().matches("\\d{10,13}"))
            throw new Exception("No HP harus 10-13 digit angka!");
    }
}