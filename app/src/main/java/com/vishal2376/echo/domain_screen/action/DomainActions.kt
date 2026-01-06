package com.vishal2376.echo.domain_screen.action

/**
 * User intent actions - no logic, just declarations
 */
sealed interface DomainActions {
    data class UpdateDomain(val domain: String) : DomainActions
    data object CheckDomain : DomainActions
    data object ClearResults : DomainActions
    data object ShareResults : DomainActions
}
