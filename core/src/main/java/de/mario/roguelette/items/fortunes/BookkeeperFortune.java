package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.BetType;
import de.mario.roguelette.betting.ColumnBet;
import de.mario.roguelette.betting.DozenBet;
import de.mario.roguelette.events.BetResolution;

/**
 * Passive: winning dozen and column bets gain a flat base-multiplier bonus (3x -&gt; 4x per copy).
 * Deliberately boosts the neglected bet family: unlike colour, dozen/column probability can't be
 * sharply engineered by recolouring the wheel, so this axis is safe to make strong (see the
 * colour-axis snowball history in the economy notes).
 */
public class BookkeeperFortune extends FortuneShopItem {

    private static final float BASE_BONUS = 1.0f;

    public BookkeeperFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/bookkeeper.png")),
            Color.GOLDENROD, new Color(0.42f, 0.26f, 0.12f, 1f)));
        this.cost = 12;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        BetType type = resolution.getBet().getBetType();
        if (type instanceof DozenBet || type instanceof ColumnBet) {
            resolution.addBase(BASE_BONUS);
        }
    }

    @Override
    public String getShortDescription() {
        return "The Bookkeeper";
    }

    @Override
    public String getDescription() {
        return "Winning dozen and column bets gain +" + BASE_BONUS
            + " to their base multiplier (3x becomes 4x).";
    }
}
