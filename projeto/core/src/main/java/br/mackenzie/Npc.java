package br.mackenzie;

import com.badlogic.gdx.math.Vector2;

public class Npc extends Agente {

    public EstadoNpc estado = EstadoNpc.VAGAR;
    public float raioVisao;
    public Agente jogador;
    public Vector2 alvoClique;
    public boolean modoClique;
    public float raioDesaceleracao = 1.5f;

    public Npc(float x, float y, float maxVelocidade, float maxForca, float raio, float raioVisao) {
        super(x, y, maxVelocidade, maxForca, raio);
        this.raioVisao = raioVisao;
        this.alvoClique = new Vector2(x, y);
    }

    public void atualizarEstado() {
        if (jogador == null) {
            estado = EstadoNpc.VAGAR;
            return;
        }
        float distancia = posicao.dst(jogador.posicao);
        if (estado == EstadoNpc.VAGAR && distancia < raioVisao) {
            estado = EstadoNpc.PERSEGUIR;
        } else if (estado == EstadoNpc.PERSEGUIR && distancia > raioVisao * 1.5f) {
            estado = EstadoNpc.VAGAR;
        }
    }

    @Override
    public void atualizar(float dt) {
        if (modoClique) {
            aplicarForca(arrive(alvoClique, raioDesaceleracao));
        } else {
            atualizarEstado();
            switch (estado) {
                case PERSEGUIR:
                    aplicarForca(pursue(jogador));
                    break;
                case VAGAR:
                default:
                    aplicarForca(wander());
                    break;
            }
        }
        super.atualizar(dt);
    }
}
