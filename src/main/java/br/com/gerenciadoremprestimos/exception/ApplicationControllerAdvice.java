package br.com.gerenciadoremprestimos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationErrors(MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        List<String> erros = bindingResult.getAllErrors()
                .stream()
                .map(obj -> obj.getDefaultMessage())
                .collect(Collectors.toList());

        return new ApiErrors(erros);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        String mensagemErro = ex.getReason();
        HttpStatusCode codigoStatus = ex.getStatusCode();
        ApiErrors apiErrors = new ApiErrors(mensagemErro);
        return new ResponseEntity(apiErrors, codigoStatus);
    }

}