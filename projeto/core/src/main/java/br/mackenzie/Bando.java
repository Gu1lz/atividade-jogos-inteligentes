package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Bando {

    public final Array<Boid> boids = new Array<>();

    public float pesoSeparacao = 1.8f;
    public float pesoAlinhamento = 1.0f;
    public float pesoCoesao = 1.0f;
    public float raioSeparacao = 0.8f;
    public float raioVizinhanca = 2.5f;

    private final float largura;
    private final float altura;

    public Bando(int quantidade, float largura, float altura) {
        this.largura = largura;
        this.altura = altura;
        for (int i = 0; i < quantidade; i++) {
            boids.add(new Boid(MathUtils.random(0f, largura), MathUtils.random(0f, altura), 2.5f, 5f, 0.15f));
        }
    }

    public void atualizar(float dt) {
        for (int i = 0; i < boids.size; i++) {
            Boid b = boids.get(i);
            Vector2 sep = b.separacao(boids, raioSeparacao).scl(pesoSeparacao);
            Vector2 ali = b.alinhamento(boids, raioVizinhanca).scl(pesoAlinhamento);
            Vector2 coe = b.coesao(boids, raioVizinhanca).scl(pesoCoesao);
            b.aplicarForca(sep);
            b.aplicarForca(ali);
            b.aplicarForca(coe);
        }
        for (int i = 0; i < boids.size; i++) {
            Boid b = boids.get(i);
            b.atualizar(dt);
            b.contornar(largura, altura);
        }
    }

    public void desenhar(ShapeRenderer sr) {
        for (int i = 0; i < boids.size; i++) {
            boids.get(i).desenhar(sr, Color.SKY);
        }
    }

    public void desenharVetores(ShapeRenderer sr) {
        for (int i = 0; i < boids.size; i++) {
            boids.get(i).desenharVetores(sr);
        }
    }
}