package bg.softuni.gamestore.service.impls;

import bg.softuni.gamestore.data.entities.Game;
import bg.softuni.gamestore.data.repositories.GameRepository;
import bg.softuni.gamestore.service.GameService;
import bg.softuni.gamestore.service.dtos.GameCreateDto;
import bg.softuni.gamestore.service.dtos.GameEditDto;
import bg.softuni.gamestore.service.dtos.GameViewDto;
import bg.softuni.gamestore.utils.ValidatorUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;

    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper, ValidatorUtil validatorUtil) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
    }


    @Override
    public String addGame(GameCreateDto gameCreateDto) {
        if (!this.validatorUtil.isValid(gameCreateDto)) {
            return this.validatorUtil.validate(gameCreateDto)
                    .stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }
        Game game = this.modelMapper.map(gameCreateDto, Game.class);
        this.gameRepository.saveAndFlush(game);
        return String.format("Added: %s", game.getTitle());
    }

    @Override
    public String editGame(GameEditDto gameEditDto) {

        if (!this.validatorUtil.isValid(gameEditDto)){
            return this.validatorUtil.validate(gameEditDto)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
        }
        Optional<Game> optionalGame = this.gameRepository.findById(gameEditDto.getId());

        if (optionalGame.isEmpty()){
            return "No such game found";
        }
        Game game = optionalGame.get();
        if (gameEditDto.getPrice() != null){
            game.setPrice(gameEditDto.getPrice());
        }

        if (gameEditDto.getSize() != null){
            game.setSize(gameEditDto.getSize());
        }
        this.gameRepository.saveAndFlush(game);
        return String.format("Edited: %s", game.getTitle());
    }

    @Override
    public String deleteGame(int id) {
        Optional<Game> game = this.gameRepository.findById(id);
        if (game.isEmpty()){
            return "No such game found";
        }
        this.gameRepository.delete(game.get());
        return String.format("Deleted: %s", game.get().getTitle());
    }

    @Override
    public Set<GameViewDto> getAllGames() {
        return this.gameRepository.findAll()
                .stream()
                .map(g -> this.modelMapper.map(g, GameViewDto.class))
                .collect(Collectors.toSet());
    }
}
