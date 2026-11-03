package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Agente {

    public final Vector2 posicao = new Vector2();
    public final Vector2 velocidade = new Vector2();
    public final Vector2 aceleracao = new Vector2();
    public final Vector2 forca = new Vector2();

    public float maxVelocidade;
    public float maxForca;
    public float raio;

    protected float angulo;
    private float anguloVagar;

    public Agente(float x, float y, float maxVelocidade, float maxForca, float raio) {
        this.posicao.set(x, y);
        this.maxVelocidade = maxVelocidade;
        this.maxForca = maxForca;
        this.raio = raio;
        this.angulo = MathUtils.random(0f, MathUtils.PI2);
        this.anguloVagar = 0f;
    }

    public void aplicarForca(Vector2 f) {
        aceleracao.add(f);
    }

    public void atualizar(float dt) {
        velocidade.mulAdd(aceleracao, dt).limit(maxVelocidade);
        posicao.mulAdd(velocidade, dt);
        if (velocidade.len2() > 0.0001f) {
            angulo = velocidade.angleRad();
        }
        forca.set(aceleracao);
        aceleracao.setZero();
    }

    public Vector2 seek(Vector2 alvo) {
        Vector2 desejada = new Vector2(alvo).sub(posicao);
        if (desejada.isZero(0.0001f)) {
            return new Vector2();
        }
        desejada.nor().scl(maxVelocidade);
        return desejada.sub(velocidade).limit(maxForca);
    }

    public Vector2 arrive(Vector2 alvo, float raioDesaceleracao) {
        Vector2 desejada = new Vector2(alvo).sub(posicao);
        float distancia = desejada.len();
        if (distancia < 0.0001f) {
            return new Vector2(velocidade).scl(-1f).limit(maxForca);
        }
        float velocidadeDesejada = maxVelocidade;
        if (distancia < raioDesaceleracao) {
            velocidadeDesejada = maxVelocidade * (distancia / raioDesaceleracao);
        }
        desejada.nor().scl(velocidadeDesejada);
        return desejada.sub(velocidade).limit(maxForca);
    }

    public Vector2 pursue(Agente alvo) {
        float distancia = alvo.posicao.dst(posicao);
        float tempo = distancia / maxVelocidade;
        Vector2 futuro = new Vector2(alvo.posicao).mulAdd(alvo.velocidade, tempo);
        return seek(futuro);
    }

    public Vector2 wander() {
        float distanciaCirculo = 1.5f;
        float raioCirculo = 0.8f;
        anguloVagar += MathUtils.random(-1f, 1f) * 0.5f;
        float direcao = velocidade.len2() > 0.0001f ? velocidade.angleRad() : angulo;
        Vector2 centro = new Vector2(MathUtils.cos(direcao), MathUtils.sin(direcao)).scl(distanciaCirculo).add(posicao);
        Vector2 deslocamento = new Vector2(MathUtils.cos(direcao + anguloVagar), MathUtils.sin(direcao + anguloVagar)).scl(raioCirculo);
        return seek(centro.add(deslocamento));
    }

    public void contornar(float largura, float altura) {
        if (posicao.x < 0) posicao.x += largura;
        if (posicao.x > largura) posicao.x -= largura;
        if (posicao.y < 0) posicao.y += altura;
        if (posicao.y > altura) posicao.y -= altura;
    }

    public void desenhar(ShapeRenderer sr, Color cor) {
        float ax = MathUtils.cos(angulo);
        float ay = MathUtils.sin(angulo);
        float bx = MathUtils.cos(angulo + 2.5f);
        float by = MathUtils.sin(angulo + 2.5f);
        float cx = MathUtils.cos(angulo - 2.5f);
        float cy = MathUtils.sin(angulo - 2.5f);
        sr.setColor(cor);
        sr.triangle(
            posicao.x + ax * raio, posicao.y + ay * raio,
            posicao.x + bx * raio, posicao.y + by * raio,
            posicao.x + cx * raio, posicao.y + cy * raio
        );
    }

    public void desenharVetores(ShapeRenderer sr) {
        sr.setColor(Color.GREEN);
        sr.line(posicao.x, posicao.y, posicao.x + velocidade.x, posicao.y + velocidade.y);
        sr.setColor(Color.RED);
        sr.line(posicao.x, posicao.y, posicao.x + forca.x * 0.5f, posicao.y + forca.y * 0.5f);
    }
}
