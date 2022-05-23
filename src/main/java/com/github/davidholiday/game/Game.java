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


//


game object

plays a round of blackjack
    * has method to play [n] rounds
    * has method to play [n] shoes

handles recording of salient game data

if a player goes bust then
  * their bankroll is refreshed
  * when they went bust is recorded
  * if it happens mid game (ie they can't double down when they normally would) the
    bust is counted as that hand.


//




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
