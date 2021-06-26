package npc.brainsynder;

public enum NPCPing {
        NO_CONNECTION(-1),
        ONE_BAR(1000),
        TWO_BARS(999),
        THREE_BARS(599),
        FOUR_BARS(299),
        FIVE_BARS(149);

        private final int milliseconds;

        NPCPing(int milliseconds) {
            this.milliseconds = milliseconds;
        }

        public int getMilliseconds() {
            return milliseconds;
        }
    }