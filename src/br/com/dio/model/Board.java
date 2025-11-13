package br.com.dio.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors(){
        if(getStatus() == NON_STARTED){
            return false;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean provideHint(){
        // Procura por um espaço que:
        // 1. Não seja fixo (!s.isFixed())
        // 2. Esteja vazio (isNull(s.getActual())) OU tenha um valor diferente do esperado
        Optional<Space> spaceToHint = spaces.stream()
                .flatMap(Collection::stream)
                .filter(s -> !s.isFixed() && (isNull(s.getActual()) || !s.getActual().equals(s.getExpected())))
                .findFirst(); // Pega o primeiro que encontrar

        // Se encontrou um espaço elegível...
        if (spaceToHint.isPresent()){
            Space space = spaceToHint.get();
            // ...define o valor 'actual' dele como o valor 'expected'
            space.setActual(space.getExpected());
            return true; // Retorna true (dica foi dada)
        }

        // Não encontrou espaços para dar dica (provavelmente o jogo está completo)
        return false; // Retorna false (dica não foi dada)
    }

    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

}
