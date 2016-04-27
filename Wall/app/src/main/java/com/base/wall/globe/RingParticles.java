package com.base.wall.globe;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseObject;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.particles.BaseParticle;
import com.base.lib.engine.particles.BaseParticleSystem;
import com.base.lib.engine.particles.EmptyParticleAction;
import com.base.lib.engine.particles.ParticleConstructor;
import com.base.lib.engine.particles.ParticleEmiter;
import com.base.wall.common.MathHelper;
import com.base.wall.listener.ColorChangedListener;
import com.base.wall.listener.WeatherChangedListener;
import com.base.wall.weather.WeatherData;

public class RingParticles extends BaseObject implements ColorChangedListener, WeatherChangedListener {

    private final BaseParticleSystem particles;
    private final Colorf particleColor;

    public RingParticles(Base base, BaseCamera camera) {
        super(base);

        particleColor = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);

        ParticleEmiter emiter = ParticleEmiter.point();
        emiter.translatePoint(0.0f, 3.25f, 0.0f);
        emiter.persist();

        particles = new BaseParticleSystem(base, 512, null, new RingParticleConstuctor(), null) {
            @Override
            public void update() {

                if (isEmitTime()) {
                    setDirectionRandomness(0.5f * random.nextFloat());
                    emiter.rotateVerticesZ(random.nextFloat() * 720.0f);
                    emiter.direct();
                    emitParticles();
                }
            }
        };

        particles.setCamera(camera);
        particles.setTexture(base.factory.getTexture("particles/particle_clear.png"), 4);
        particles.setEmiter(emiter);
        particles.setContiniously(true);
        particles.setParticleCountPerEmit(1);
        particles.setSpeed(0.25f, 0.5f);
        particles.setScale(0.15f, 0.15f);
        particles.setScaleStep(0.15f, -0.3f);
        particles.setLifeTime(1250, 500);
        particles.setEmitDelay(55);
    }

    public BaseParticleSystem getInstance() {

        return particles;
    }

    @Override
    public void onColorChanged(Colorf color) {

        particleColor.r = color.r;
        particleColor.g = color.g;
        particleColor.b = color.b;
    }

    @Override
    public void onWeatherChanged(WeatherData data) {

        //current   speed (0.25 0.5)        lifetime (1250 500)     emit 25
        //min       speed (0.125 0.125)     lifetime (3250 500)     emit 100
        //max       speed (1.25 0.15)       lifetime (750 150)      emit 25

        double t = data.getWindSpeed() / 75.0;
        float speed = MathHelper.interpolate(0.125, 1.25, t);
        float speedOffset = MathHelper.interpolate(0.125, 0.15, t);
        long lifetime = (long) MathHelper.interpolate(3250, 750, t);
        long lifetimeOffset = (long) MathHelper.interpolate(500, 150, t);

        String textureName;

        switch (data.getIcon()) {
            case CLEAR:
                textureName = "particles/particle_clear.png";
                break;
            case CLOUDY:
                textureName = "particles/particle_cloudy.png";
                break;
            case PARTLY_CLOUDY:
                textureName = "particles/particle_cloudy_partly.png";
                break;
            case RAIN:
                textureName = "particles/rain_particle.png";
                break;
            case SNOW:
                textureName = "particles/snow_particle.png";
                break;
            case WIND:
                textureName = "particles/particle_wind.png";
                break;
            case FOG:
                textureName = "particles/particle_sleet.png";
                break;
            case SLEET:
                textureName = "particles/particle_sleet.png";
                break;
            default:
                textureName = "particles/particle_clear.png";
        }

        particles.setSpeed(speed, speedOffset);
        particles.setLifeTime(lifetime, lifetimeOffset);
        particles.setTexture(base.factory.getTexture(textureName), 4);
    }

    class RingParticle extends BaseParticle {

        BaseParticle init() {

            action = new EmptyParticleAction() {
                @Override
                public void onActionEnds() {
                    inUse = false;
                }

                @Override
                public void update(float delta, float delay) {
                    actionRemainingTime -= delay;

                    if (actionRemainingTime > 0) {
                        progress = (float) actionRemainingTime / (float) actionLifeTime;
                        data[0] += x * delta;
                        data[1] += y * delta;
                        data[2] += z * delta;
                        data[3] += s * delta;

                        data[9] = 0.5f * progress;
                    } else {
                        action.onActionEnds();
                    }
                }
            };

            return this;
        }

        @Override
        protected void init(BaseParticleSystem ps, int emiterOffset) {
            super.init(ps, emiterOffset);

            data[6] = particleColor.r;
            data[7] = particleColor.g;
            data[8] = particleColor.b;
        }
    }

    class RingParticleConstuctor implements ParticleConstructor {
        @Override
        public BaseParticle newInstance() {
            return new RingParticle().init();
        }
    }
}
