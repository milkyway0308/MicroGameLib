package skywolf46.microgamelib.enums

enum class ExtractScope(val extractInstant: Boolean) {
    CURRENT_STAGE(false),
    NEXT_STAGE(true),
    CURRENT_GAME(false),
    NEXT_GAME(true),
    CURRENT_GLOBAL(false),
    NEXT_GLOBAL(true);
}