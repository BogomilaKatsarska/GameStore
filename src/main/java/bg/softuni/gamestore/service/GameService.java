package bg.softuni.gamestore.service;

import bg.softuni.gamestore.service.dtos.GameCreateDto;
import bg.softuni.gamestore.service.dtos.GameEditDto;
import bg.softuni.gamestore.service.dtos.GameViewDto;

import java.util.Set;

public interface GameService {
    String addGame(GameCreateDto gameCreateDto);

    String editGame(GameEditDto gameEditDto);

    String deleteGame(int id);

    Set<GameViewDto> getAllGames();
}
