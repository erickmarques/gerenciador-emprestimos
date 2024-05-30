package br.com.gerenciadoremprestimos.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.gerenciadoremprestimos.model.Usuario;
import br.com.gerenciadoremprestimos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService  {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        
        Usuario usuario = usuarioRepository.findByLogin(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Login/Senha inválidos!"));
        
        return usuario;
    }
}
