package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Game {

    public static final int IN_GAME_CARDS_DRAWN = 2;

    /**
     * Plays a game of tCHu to the given players
     *
     * @param players:     tCHu's players
     * @param playerNames: names of the given players
     * @param tickets:     available tickets for the game
     * @param rng:         random used to create a random initial state
     *                     and to shuffle the cards and discards
     * @throws IllegalArgumentException when one of the two maps' size
     *                                  is different than two
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {

        //--------------------------------- CHECKS CONDITIONS ----------------------------------------------

        Preconditions.checkArgument(players.size() == PlayerId.COUNT
                && playerNames.size() == PlayerId.COUNT);

        //--------------------------------- Initialize game ----------------------------------------------------
        //INIT PLAYERS


        players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));

        //Map playerId - Info
        Map<PlayerId, Info> playerInfos = new EnumMap<>(PlayerId.class);

        //Initialises players' names
        playerNames.forEach((playerId, playerName) ->
                playerInfos.put(playerId, new Info(playerName))
        );

        //set GameState Initial
        GameState gameState = GameState.initial(tickets, rng);

        //receiveInfo of who plays first
        sendInfo(players, playerInfos
                .get(gameState.currentPlayerId())
                .willPlayFirst());

        updateState(players, gameState);

        //Set initial ticket choice  for each player

        for (Map.Entry<PlayerId, Player> p : players.entrySet()) {
            SortedBag<Ticket> initialTickets = gameState.topTickets(Constants.INITIAL_TICKETS_COUNT);
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            p.getValue().setInitialTicketChoice(initialTickets);
        }
        updateState(players, gameState);

        //choose initial ticket for each player
        List<String> nbrChosenTickets = new ArrayList<>();
        for (Map.Entry<PlayerId, Player> p : players.entrySet()) {
            SortedBag<Ticket> chosenTickets = p.getValue().chooseInitialTickets();
            nbrChosenTickets.add(playerInfos.get(p.getKey()).keptTickets(chosenTickets.size()));
        }
        nbrChosenTickets.forEach(s -> sendInfo(players, s));


        //--------------------------------- Start game --------------------------------------------------

        //boolean that indicates the last turn of tCHu
        boolean finalTurn = false;

        while (true) {
            Player currentPlayer = players.get(gameState.currentPlayerId());

            //info for player's turn
            sendInfo(players, playerInfos
                    .get(gameState.currentPlayerId())
                    .canPlay());

            updateState(players, gameState);
            Player.TurnKind turnKind = currentPlayer.nextTurn();

            switch (turnKind) {

                case DRAW_TICKETS:

                    SortedBag<Ticket> options = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    //tickets the player drew from top tickets
                    sendInfo(players, playerInfos
                            .get(gameState.currentPlayerId())
                            .drewTickets(options.size()));

                    SortedBag<Ticket> ticketsChosen = currentPlayer
                            .chooseTickets(options);
                    //how many tickets the player kept
                    sendInfo(players, playerInfos
                            .get(gameState.currentPlayerId())
                            .keptTickets(ticketsChosen.size()));
                    gameState = gameState.withChosenAdditionalTickets(options, ticketsChosen);

                    updateState(players, gameState);
                    break;

                case DRAW_CARDS:

                    for (int i = 0; i < IN_GAME_CARDS_DRAWN; ++i) {
                        int drawSlot = currentPlayer.drawSlot();

                        //Checks where the player wants to pick the card from
                        if (drawSlot == Constants.DECK_SLOT) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            updateState(players, gameState);

                            sendInfo(players, playerInfos
                                    .get(gameState.currentPlayerId())
                                    .drewBlindCard());

                            gameState = gameState.withBlindlyDrawnCard();
                        }

                        if (drawSlot >= 0
                                && drawSlot < Constants.FACE_UP_CARDS_COUNT) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            updateState(players, gameState);

                            sendInfo(players, playerInfos
                                    .get(gameState.currentPlayerId())
                                    .drewVisibleCard(gameState
                                            .cardState()
                                            .faceUpCard(drawSlot)));

                            gameState = gameState.withDrawnFaceUpCard(drawSlot);
                        }
                        updateState(players, gameState);
                    }
                    break;

                case CLAIM_ROUTE:

                    Route routeClaimed = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                    if (routeClaimed.level() == Route.Level.UNDERGROUND) {
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();

                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {

                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        updateState(players, gameState);

                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                        updateState(players, gameState);

                        int additionalCost = routeClaimed.additionalClaimCardsCount(initialCards, drawnCards);

                        sendInfo(players, playerInfos
                                .get(gameState.currentPlayerId())
                                .drewAdditionalCards(drawnCards, additionalCost));

                        if (additionalCost == 0) {
                            gameState = gameState.withClaimedRoute(routeClaimed, initialCards);
                            updateState(players, gameState);
                            sendInfo(players,
                                    playerInfos.get(gameState.currentPlayerId())
                                            .claimedRoute(routeClaimed, initialCards));
                            break;
                        }
                        var possAddCards = gameState.currentPlayerState()
                                .possibleAdditionalCards(additionalCost, initialCards, drawnCards);

                        if (possAddCards.isEmpty()) {
                            sendInfo(players, playerInfos
                                    .get(gameState.currentPlayerId())
                                    .didNotClaimRoute(routeClaimed));

                            break;
                        }
                        SortedBag<Card> selectedOption = currentPlayer
                                .chooseAdditionalCards(possAddCards);

                        if (selectedOption.isEmpty()) {
                            sendInfo(players, playerInfos
                                    .get(gameState.currentPlayerId())
                                    .didNotClaimRoute(routeClaimed));
                            break;
                        }
                        gameState = gameState.withClaimedRoute(routeClaimed, selectedOption.union(initialCards));
                    }
                    else {
                        gameState = gameState.withClaimedRoute(routeClaimed, initialCards);
                        updateState(players, gameState);
                    }
                    sendInfo(players,
                            playerInfos.get(gameState.currentPlayerId()).claimedRoute(routeClaimed, initialCards));
                    break;
            }
            if (finalTurn) break;

            if (gameState.lastTurnBegins()) {
                sendInfo(players, playerInfos
                        .get(gameState.currentPlayerId())
                        .lastTurnBegins(gameState.currentPlayerState()
                                .carCount()));
            }
            gameState = gameState.forNextTurn();
            updateState(players, gameState);

            if (gameState.currentPlayerId() == gameState.lastPlayer()) finalTurn = true;
        }

       // ----------------------------------End Game--------------------------------------------------------

        Map<PlayerId, Trail> playerTrail = new EnumMap<>(PlayerId.class);
        List<Integer> longestTrail = new ArrayList<>();

        for (PlayerId id : PlayerId.ALL) {
            playerTrail.put(id, Trail.longest(gameState.playerState(id).routes()));
            longestTrail.add(playerTrail.get(id).length());
        }

        //----------------------------------Score Count-----------------------------------------------------

        //checks which player has the longest trail
        boolean player1Bonus = false;
        boolean player2Bonus = false;
        int diffTrail = longestTrail.get(0) - longestTrail.get(1);
        //if they have equally long trails, both get the bonus
        if (diffTrail == 0) {
            sendInfo(players, playerInfos
                    .get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(playerTrail.get(PlayerId.PLAYER_1)));

            sendInfo(players, playerInfos
                    .get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(playerTrail.get(PlayerId.PLAYER_2)));

            player1Bonus = true;
            player2Bonus = true;
        }
        //if the first player has a longer trail, he gets the bonus
        else if (diffTrail > 0) {
            sendInfo(players, playerInfos
                    .get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(playerTrail.get(PlayerId.PLAYER_1)));
            player1Bonus = true;
        }
        //if the second player has a longer trail, he gets the bonus
        else {
            sendInfo(players, playerInfos
                    .get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(playerTrail.get(PlayerId.PLAYER_2)));
            player2Bonus = true;
        }
        //gives the first player his points depending on if he gets the bonus
        int pointsPlayer1 = (player1Bonus) ? gameState.playerState(PlayerId.PLAYER_1)
                .finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS
                : gameState.playerState(PlayerId.PLAYER_1).finalPoints();

        //gives the second player his points depending on if he gets the bonus
        int pointsPlayer2 = (player2Bonus) ? gameState.playerState(PlayerId.PLAYER_2)
                .finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS
                : gameState.playerState(PlayerId.PLAYER_2).finalPoints();


        //----------------------------------Declares Winner-------------------------------------------

        //if both player have the same number of points, the game declares that there
        //is a draw
        if (pointsPlayer1 == pointsPlayer2) {
            List<String> listNames = new ArrayList<>();
            for (PlayerId id : playerNames.keySet()) {
                listNames.add(playerNames.get(id));
            }
            sendInfo(players, Info.draw(listNames, pointsPlayer1));
        }
        //if the player do not have the same points, the game declares the winner by
        //comparing both of the players points
        else {
            if (pointsPlayer1 > pointsPlayer2) {
                sendInfo(players, playerInfos.get(PlayerId.PLAYER_1).won(pointsPlayer1, pointsPlayer2));
            } else {
                sendInfo(players, playerInfos.get(PlayerId.PLAYER_2).won(pointsPlayer2, pointsPlayer1));
            }
        }
    }

    private static void updateState(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((idPlayer, player) -> player.updateState(gameState, gameState.playerState(idPlayer)));
    }

    private static void sendInfo(Map<PlayerId, Player> players, String info) {
        players.values().forEach(player -> player.receiveInfo(info));
    }
}