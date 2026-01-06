package com.vishal2376.echo.domain_screen.events

/**
 * One-time side effects exposed as Flow
 */
sealed interface DomainEvents {
    data class ShowError(val message: String) : DomainEvents
    data class CopiedToClipboard(val content: String) : DomainEvents
    data class ShowToast(val message: String) : DomainEvents
    data class ShareText(val text: String) : DomainEvents
}
