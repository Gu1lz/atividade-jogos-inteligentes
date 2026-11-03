package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {

    private static final float LARGURA = 16f;
    private static final float ALTURA = 9f;

    private FitViewport viewport;
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private BitmapFont fonte;

    private Npc npcSeek;
    private Npc npcFsm;
    private Agente jogador;
    private Bando bando;

    private final Vector2 mouse = new Vector2();
    private int modo = 1;
    private boolean depurar = true;

    @Override
    public void create() {
        viewport = new FitViewport(LARGURA, ALTURA);
        sr = new ShapeRenderer();
        batch = new SpriteBatch();
        fonte = new BitmapFont();
        fonte.setUseIntegerPositions(false);
        fonte.getData().setScale(LARGURA / 800f * 1.2f);

        npcSeek = new Npc(LARGURA / 2f, ALTURA / 2f, 4f, 8f, 0.3f, 0f);
        npcSeek.modoClique = true;
        npcSeek.alvoClique.set(LARGURA / 2f, ALTURA / 2f);

        jogador = new Agente(2f, 2f, 3.5f, 20f, 0.3f);

        npcFsm = new Npc(LARGURA - 3f, ALTURA - 3f, 3f, 6f, 0.3f, 3f);
        npcFsm.jogador = jogador;

        bando = new Bando(30, LARGURA, ALTURA);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Math.min(Gdx.graphics.getDeltaTime(), 0.05f);
        lerEntrada();
        atualizar(dt);
        desenhar();
    }

    private void lerEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) modo = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) modo = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) modo = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) depurar = !depurar;

        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);
    }

    private void atualizar(float dt) {
        switch (modo) {
            case 1:
                if (Gdx.input.isTouched()) {
                    npcSeek.alvoClique.set(mouse);
                }
                npcSeek.atualizar(dt);
                npcSeek.contornar(LARGURA, ALTURA);
                break;
            case 2:
                atualizarJogador(dt);
                npcFsm.atualizar(dt);
                npcFsm.contornar(LARGURA, ALTURA);
                break;
            case 3:
                bando.atualizar(dt);
                break;
        }
    }

    private void atualizarJogador(float dt) {
        float dx = 0f;
        float dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1f;

        if (dx != 0f || dy != 0f) {
            jogador.velocidade.set(dx, dy).nor().scl(jogador.maxVelocidade);
            jogador.aceleracao.setZero();
        } else if (Gdx.input.isTouched()) {
            jogador.aplicarForca(jogador.arrive(mouse, 1.5f));
        } else {
            jogador.velocidade.scl(0.9f);
        }
        jogador.atualizar(dt);
        jogador.contornar(LARGURA, ALTURA);
    }

    private void desenhar() {
        ScreenUtils.clear(0.1f, 0.1f, 0.12f, 1f);
        viewport.apply();
        sr.setProjectionMatrix(viewport.getCamera().combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        switch (modo) {
            case 1:
                sr.setColor(Color.GOLD);
                sr.circle(npcSeek.alvoClique.x, npcSeek.alvoClique.y, 0.1f, 16);
                npcSeek.desenhar(sr, Color.ORANGE);
                break;
            case 2:
                jogador.desenhar(sr, Color.GREEN);
                npcFsm.desenhar(sr, npcFsm.estado == EstadoNpc.PERSEGUIR ? Color.RED : Color.YELLOW);
                break;
            case 3:
                bando.desenhar(sr);
                break;
        }
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        switch (modo) {
            case 1:
                if (depurar) npcSeek.desenharVetores(sr);
                break;
            case 2:
                sr.setColor(npcFsm.estado == EstadoNpc.PERSEGUIR ? Color.RED : Color.DARK_GRAY);
                sr.circle(npcFsm.posicao.x, npcFsm.posicao.y, npcFsm.raioVisao, 48);
                if (depurar) {
                    npcFsm.desenharVetores(sr);
                    jogador.desenharVetores(sr);
                }
                break;
            case 3:
                if (depurar) bando.desenharVetores(sr);
                break;
        }
        sr.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, textoModo(), 0.2f, ALTURA - 0.2f);
        fonte.draw(batch, "1: Seek/Arrive   2: Wander + FSM   3: Bando   F: vetores", 0.2f, 0.5f);
        batch.end();
    }

    private String textoModo() {
        switch (modo) {
            case 1:
                return "Modo 1 - Seek e Arrive (clique para mover o NPC)";
            case 2:
                return "Modo 2 - Wander + FSM (estado: " + npcFsm.estado + ")";
            default:
                return "Modo 3 - Bando (separacao, alinhamento e coesao)";
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
        fonte.dispose();
    }
}
