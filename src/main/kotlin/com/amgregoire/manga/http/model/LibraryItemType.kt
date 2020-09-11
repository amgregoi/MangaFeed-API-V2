package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "library_item_types")
class LibraryItemType(
        @NotBlank
        var type: String = "",

        var color: Long = 0xffffffff
) : AuditModel()

enum class LibraryItemTypes(val type: String)
{
    PlanToRead("Plan to read"),
    OnHold("On hold"),
    Complete("Complete"),
    Reading("Reading");

    companion object
    {
        fun fromType(type: String): LibraryItemTypes
        {
            return when
            {
                type.equals(PlanToRead.type, true) -> PlanToRead
                type.equals(OnHold.type, true) -> OnHold
                type.equals(Complete.type, true) -> Complete
                type.equals(Reading.type, true) -> Reading
                else -> Reading
            }
        }
    }
}
