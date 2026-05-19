package com.example.onlinelibrary.config;

import com.example.onlinelibrary.model.Book;
import com.example.onlinelibrary.model.Role;
import com.example.onlinelibrary.model.User;
import com.example.onlinelibrary.repository.BookRepository;
import com.example.onlinelibrary.repository.UserRepository;
import com.example.onlinelibrary.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUtils imageUtils;

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "adminpass", Role.ADMIN);
        createUserIfMissing("user", "userpass", Role.USER);

        seedBooks();
    }

    private void seedBooks() {
        Object[][] books = {
                {"Don Quijote de la Mancha", "Miguel de Cervantes", 1605, "#86efac"},
                {"Cien anos de soledad", "Gabriel Garcia Marquez", 1967, "#fde68a"},
                {"La sombra del viento", "Carlos Ruiz Zafon", 2001, "#bfdbfe"},
                {"Rayuela", "Julio Cortazar", 1963, "#fecdd3"},
                {"Ficciones", "Jorge Luis Borges", 1944, "#ddd6fe"},
                {"El amor en los tiempos del colera", "Gabriel Garcia Marquez", 1985, "#fed7aa"},
                {"La casa de los espiritus", "Isabel Allende", 1982, "#bbf7d0"},
                {"Pedro Paramo", "Juan Rulfo", 1955, "#c7d2fe"},
                {"El Aleph", "Jorge Luis Borges", 1949, "#bae6fd"},
                {"Los detectives salvajes", "Roberto Bolano", 1998, "#fecaca"},
                {"2666", "Roberto Bolano", 2004, "#e9d5ff"},
                {"Nada", "Carmen Laforet", 1945, "#fde68a"},
                {"La ciudad y los perros", "Mario Vargas Llosa", 1963, "#a7f3d0"},
                {"Conversacion en La Catedral", "Mario Vargas Llosa", 1969, "#bfdbfe"},
                {"Como agua para chocolate", "Laura Esquivel", 1989, "#fbcfe8"},
                {"El tunel", "Ernesto Sabato", 1948, "#ddd6fe"},
                {"Sobre heroes y tumbas", "Ernesto Sabato", 1961, "#fed7aa"},
                {"La tregua", "Mario Benedetti", 1960, "#bbf7d0"},
                {"Primavera con una esquina rota", "Mario Benedetti", 1982, "#bae6fd"},
                {"Aura", "Carlos Fuentes", 1962, "#fecaca"},
                {"La region mas transparente", "Carlos Fuentes", 1958, "#e9d5ff"},
                {"El coronel no tiene quien le escriba", "Gabriel Garcia Marquez", 1961, "#fde68a"},
                {"Cronica de una muerte anunciada", "Gabriel Garcia Marquez", 1981, "#a7f3d0"},
                {"Platero y yo", "Juan Ramon Jimenez", 1914, "#bfdbfe"},
                {"Bodas de sangre", "Federico Garcia Lorca", 1933, "#fbcfe8"},
                {"La colmena", "Camilo Jose Cela", 1951, "#ddd6fe"},
                {"Tiempo de silencio", "Luis Martin-Santos", 1962, "#fed7aa"},
                {"El camino", "Miguel Delibes", 1950, "#bbf7d0"},
                {"Cinco horas con Mario", "Miguel Delibes", 1966, "#bae6fd"},
                {"Fortunata y Jacinta", "Benito Perez Galdos", 1887, "#fecaca"},
                {"La Regenta", "Leopoldo Alas Clarin", 1885, "#e9d5ff"},
                {"Niebla", "Miguel de Unamuno", 1914, "#a7f3d0"}
        };

        for (Object[] book : books) {
            String title = (String) book[0];
            if (!bookRepository.existsByTitle(title)) {
                bookRepository.save(seedBook(title, (String) book[1], (Integer) book[2], (String) book[3]));
            }
        }
    }

    private Book seedBook(String title, String author, Integer year, String color) {
        return Book.builder()
                .title(title)
                .author(author)
                .year(year)
                .thumbnail(imageUtils.fallbackCover(title, color))
                .build();
    }

    private void createUserIfMissing(String username, String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
        }
    }
}
