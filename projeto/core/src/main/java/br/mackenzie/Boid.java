package br.mackenzie;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Boid extends Agente {

    public Boid(float x, float y, float maxVelocidade, float maxForca, float raio) {
        super(x, y, maxVelocidade, maxForca, raio);
        velocidade.set(1f, 0f).setAngleRad(angulo).scl(maxVelocidade);
    }

    public Vector2 separacao(Array<Boid> bando, float raioSeparacao) {
        Vector2 soma = new Vector2();
        int contagem = 0;
        for (int i = 0; i < bando.size; i++) {
            Boid outro = bando.get(i);
            if (outro == this) continue;
            float d = posicao.dst(outro.posicao);
            if (d > 0f && d < raioSeparacao) {
                Vector2 afastar = new Vector2(posicao).sub(outro.posicao).nor().scl(1f / d);
                soma.add(afastar);
                contagem++;
            }
        }
        if (contagem == 0) return new Vector2();
        soma.nor().scl(maxVelocidade).sub(velocidade);
        return soma.limit(maxForca);
    }

    public Vector2 alinhamento(Array<Boid> bando, float raioVizinhanca) {
        Vector2 soma = new Vector2();
        int contagem = 0;
        for (int i = 0; i < bando.size; i++) {
            Boid outro = bando.get(i);
            if (outro == this) continue;
            if (posicao.dst(outro.posicao) < raioVizinhanca) {
                soma.add(outro.velocidade);
                contagem++;
            }
        }
        if (contagem == 0) return new Vector2();
        soma.nor().scl(maxVelocidade).sub(velocidade);
        return soma.limit(maxForca);
    }

    public Vector2 coesao(Array<Boid> bando, float raioVizinhanca) {
        Vector2 centro = new Vector2();
        int contagem = 0;
        for (int i = 0; i < bando.size; i++) {
            Boid outro = bando.get(i);
            if (outro == this) continue;
            if (posicao.dst(outro.posicao) < raioVizinhanca) {
                centro.add(outro.posicao);
                contagem++;
            }
        }
        if (contagem == 0) return new Vector2();
        centro.scl(1f / contagem);
        return seek(centro);
    }
}