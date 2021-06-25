package skywolf46.microgamelib.annotations

import skywolf46.microgamelib.enums.InjectScope

/**
 * Extract annotated field to target scope when stage ended.
 * [InjectScope.STAGE] will not work cause STAGE argument remove when stage finished.
 */
annotation class Extract(val scope: InjectScope = InjectScope.GAME) {
}