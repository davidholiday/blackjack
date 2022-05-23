package com.github.davidholiday.game;

public class Game {

    /*
    container for:
        * players
          * hand
          * bankroll
          * strategy
        * dealer
          * shoe
          * bankroll
          * strategy
        * game rule set
        * dealer/player action queues
        * dealer/player card queues
        * dealer/player bank queues

    gameStart() method that activates the dealer

Map<PlayerPosition, Agent> playerMap

Optional<List<Cards>> getOfferedCardsFrom(PlayerPosition)


*/

    public class GamePublic {
        /*
        nested inner class GamePublic?
                this way a view object can be created of the game objects data w/o exposing the whole
        game object
        Map<PlayerPosition, Action> actionMap,
        Map<PlayerPosition, Hand> handsMap,
        Map<PlayerPosition, List<Card>> offeredCardsMap,
        Map<PlayerPosition, Integer> offeredMoneyMap,
        Set<Rule> ruleSet,
        Optional<Integer> count


     */
    }


    private GamePublic updateGamePublic(GamePublic gamePublic) {
        return null;
    }

}
