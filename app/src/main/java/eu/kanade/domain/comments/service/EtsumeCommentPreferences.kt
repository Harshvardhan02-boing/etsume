package eu.kanade.domain.comments.service

import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.preference.PreferenceStore

class EtsumeCommentPreferences(
    private val preferenceStore: PreferenceStore,
) {

    fun commentDraft(key: String) = preferenceStore.getString(
        Preference.appStateKey("etsume_comment_draft_$key"),
        "",
    )

    fun commentSpoiler(key: String) = preferenceStore.getBoolean(
        Preference.appStateKey("etsume_comment_spoiler_$key"),
        false,
    )
}
