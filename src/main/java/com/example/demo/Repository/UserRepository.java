package com.example.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.User;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    private static final RowMapper<User> ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                rs.getLong("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("login"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getLong("idRole"),
                rs.getBoolean("isBanned")
            );
        }
    };

    /**
     * Vrací seznam všech uživatelů z tabulky `users`.
     * Používá parametrizovaný SQL dotaz bez jakékoli filtrace.
     */
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", ROW_MAPPER);
    }

    /**
     * Vrací uživatele podle jeho ID. Pokud uživatel neexistuje, vrátí Optional prázdný.
     * Používá parametrizovaný dotaz k zajištění bezpečnosti.
     */
    public Optional<User> getUserById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    /**
     * Aktualizuje přihlašovací údaje a e-mail uživatele podle jeho ID.
     * Tato metoda je označena jako `@Transactional`, což znamená, že pokud dojde k chybě během
     * aktualizace, všechny změny budou zrušeny.
     */
    @Transactional
    public int updateLoginAndEmail(Long userId, String login, String email) {
        return jdbcTemplate.update(
            "UPDATE users SET login = ?, email = ? WHERE id = ?",
            login, email, userId
        );
    }

    /**
     * Aktualizuje heslo uživatele. Heslo je před uložením do databáze šifrováno.
     * Pokud je předané heslo prázdné nebo null, je vyhozena výjimka `IllegalArgumentException`.
     * Metoda je označena jako `@Transactional`, což znamená, že změny budou provedeny v rámci transakce.
     */
    @Transactional
    public int updatePassword(Long userId, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        return jdbcTemplate.update(
            "UPDATE users SET password = ? WHERE id = ?",
            hashed, userId
        );
    }






   

 
   /**
     * Ukládá uživatele do databáze. Pokud uživatel nemá ID (nový uživatel), provádí INSERT,
     * jinak provádí UPDATE. Heslo uživatele je před uložením šifrováno.
     * Tato metoda je označena jako `@Transactional`, což znamená, že všechny změny se provádějí v rámci transakce.
     */
    @Transactional
    public int saveUser(User user) {
        // Získání surového hesla a jeho šifrování
        String rawPassword = user.getPassword();
        if(rawPassword == null) return -1; // Pokud není heslo, vrací -1, což značí chybu
        String hashedPassword = passwordEncoder.encode(rawPassword);

        int rowsAffected;
        Long userId;

        if (user.getId() == null) { // Nový uživatel
            // Vkládání nového uživatele do databáze
            rowsAffected = jdbcTemplate.update("INSERT INTO users (firstName, lastName, login, password, email, idRole) VALUES (?, ?, ?, ?, ?, ?)",
                    user.getFirstName(), user.getLastName(), user.getLogin(), hashedPassword, user.getEmail(), user.getIdRole());

            if (rowsAffected > 0) {
                // Získání posledního vloženého ID (pro případ, že by bylo potřeba vrátit ID nového uživatele)
                userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            } else {
                // Pokud selže vkládání uživatele, vyvoláme výjimku
                throw new RuntimeException("Failed to insert new user");
            }
        } else { // Existující uživatel (UPDATE)
            // Aktualizace údajů existujícího uživatele
            rowsAffected = jdbcTemplate.update("UPDATE users SET firstName = ?, lastName = ?, login = ?, password = ?, email = ?, idRole = ?, isBanned = ? WHERE id = ?",
                    user.getFirstName(), user.getLastName(), user.getLogin(), hashedPassword, user.getEmail(), user.getIdRole(), user.getIsBanned(), user.getId());
            userId = user.getId(); // ID se nemění, takže použijeme stávající
        }

        // Vrací počet ovlivněných řádků
        return rowsAffected;
    }

    /**
     * Smaže uživatele podle jeho ID.
     * Vrací počet ovlivněných řádků.
     */
    public int deleteUserById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        return rowsAffected;
    }

    /**
     * Vyhledá uživatele podle části jména, loginu nebo příjmení. Filtruje také záznamy, kde je uživatel zabanovaný (isBanned = false).
     * Používá parametrizovaný dotaz pro prevenci SQL injection.
     */
    public List<User> findByNameContaining(String term) {
        String sql = "SELECT * FROM users WHERE (firstName LIKE ? OR lastName LIKE ? OR login LIKE ?) AND isBanned = false";

        // Přidání zástupného znaku pro vyhledávání (procento) na začátek a konec hledaného termínu
        String searchTerm = "%" + term + "%";

        // Parametrizovaný dotaz s třemi parametry, aby se předešlo SQL injection
        return jdbcTemplate.query(sql, new Object[]{searchTerm, searchTerm, searchTerm}, ROW_MAPPER);
    }

    /**
     * Vyhledá uživatele podle loginu. Pokud uživatel existuje, vrátí ho jako Optional, jinak Optional prázdný.
     * Používá parametrizovaný dotaz pro prevenci SQL injection.
     */
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        List<User> users = jdbcTemplate.query(sql, ROW_MAPPER, login);
        return users.stream().findFirst();
    }


    /**
     * Tato metoda vrací seznam 5 uživatelů s nejvyšší frekvencí příspěvků na základě počtu příspěvků
     * v tabulce `posts`. Nejprve se provede dotaz pro získání ID uživatelů s nejvyšším počtem příspěvků
     * a následně se pomocí těchto ID vyhledají detaily uživatelů v tabulce `users`.
     */
    public List<User> getTopUsersByPostFrequency() {
        // Krok 1: Získání 5 nejčastějších idUser
        String sql = "SELECT id_user, COUNT(*) AS frequency FROM posts GROUP BY id_user ORDER BY frequency DESC LIMIT 5";
        List<Long> topUserIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id_user"));

        // Krok 2: Získání uživatelů podle idUser
        if (topUserIds.isEmpty()) {
            return List.of(); // Pokud nejsou nalezeni žádní uživatelé, vrátí prázdný seznam
        }

        // Vytvoření SQL dotazu s IN klauzulí pro získání uživatelů
        String inSql = String.join(",", topUserIds.stream().map(String::valueOf).toArray(String[]::new));
        String usersSql = "SELECT id, firstName, lastName, login, password, email, idRole, isBanned FROM users WHERE id IN (" + inSql + ")";

        return jdbcTemplate.query(usersSql, ROW_MAPPER);
    }

    public String getLoginByIdUser(int id){
        String sql = "SELECT login FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id},String.class);
    }




}

