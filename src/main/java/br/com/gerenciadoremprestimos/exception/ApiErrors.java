package br.com.gerenciadoremprestimos.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrors {

    private List<String> errors;

    public ApiErrors(String msg){
        this.errors = Arrays.asList(msg);
    }
}