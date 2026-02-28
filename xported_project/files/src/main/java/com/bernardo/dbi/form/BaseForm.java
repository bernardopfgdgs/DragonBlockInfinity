package com.bernardo.dbi.form;

public class BaseForm {
    // forma base (sem boost, só mastery)
    public static final String ID = "Base";
    // multiplicadores de status
    public float strMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float conMulti = 1.0f;
    public float willMulti = 1.0f;
    public float mnd = 1.0f;
    public float spi = 1.0f;
    public FormMastery mastery = new FormMastery();

    public class FormMastery {
        private float exp = 0.0f; // experiência acumulada
        private float level; // 0.0 to 50.0
        private static final float EXP_PER_LEVEL = 100f;

        public FormMastery() {
            this.level = 0.0f;
        }

        public float getFactor() {
            return level / 50.0f; // 0.0 to 1.0
        }

        public void addExp(float amount) {
            if (amount > 0) {
                exp += amount;
                level = Math.min(50.0f, exp / EXP_PER_LEVEL);
            }
        }

        public float getLevel() {
            return this.level;
        }

        public float getExp() {
            return this.exp;
        }

        public float getExpToNextLevel() {
            if (level >= 50.0f) return 0f;
            float nextLevelExp = ((int) level + 1) * EXP_PER_LEVEL;
            return nextLevelExp - exp;
        }
    }
}
