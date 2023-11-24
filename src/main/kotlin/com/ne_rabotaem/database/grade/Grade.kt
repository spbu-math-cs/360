package com.ne_rabotaem.database.grade

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.demo.CommentReceiveRemote
import com.ne_rabotaem.features.demo.GradeResponseRemote
import com.ne_rabotaem.features.demo.StatisticsReceiveRemote
import com.ne_rabotaem.features.demo.StatisticsResponseRemote
import com.ne_rabotaem.features.vote.PersonDemoGradeResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Demo_grade : IntIdTable("Demo_grade") {
    private val eventId = reference("event_id", Event.id)
    private val personId = reference("person_id", User.id)
    private val teamId = reference("team_id", Team.id)
    private val level = integer("level")
    private val grade = integer("grade")
    private val presentation = integer("presentation")
    private val additional = integer("additional")
    private val comment = varchar("comment", 500)

    fun insert(gradeDTO: GradeDTO) {
        transaction {
            insert {
                it[eventId] = gradeDTO.eventId
                it[personId] = gradeDTO.personId
                it[teamId] = gradeDTO.teamId
                it[level] = gradeDTO.level
                it[grade] = gradeDTO.grade
                it[presentation] = gradeDTO.presentation
                it[additional] = gradeDTO.additional
                it[comment] = gradeDTO.comment.orEmpty()
            }
        }
    }

    fun fetch(eventId: Int): List<GradeResponseRemote> {
        return transaction {
            Demo_grade.select { Demo_grade.eventId.eq(eventId) }.toList().map {
                GradeResponseRemote(
                    it[Demo_grade.id].value,
                    it[personId].value,
                    it[teamId].value,
                    it[level],
                    it[grade],
                    it[presentation],
                    it[additional],
                    it[comment],
                )
            }
        }
    }

    // needs a test
    fun getId(eventId: Int, userId: Int, teamId: Int): Int? {
        return transaction {
            val query = Demo_grade.select {
                Demo_grade.eventId.eq(eventId) and Demo_grade.personId.eq(userId) and Demo_grade.teamId.eq(teamId)
            }
            if (query.none())
                return@transaction null

            query.single().run {
                this[Demo_grade.id].value
            }
        }
    }

    fun update(gradeId: Int, gradeDTO: GradeDTO) {
        transaction {
            update( { Demo_grade.id eq gradeId } ) {
                it[level] = gradeDTO.level
                it[grade] = gradeDTO.grade
                it[presentation] = gradeDTO.presentation
                it[additional] = gradeDTO.additional
                it[comment] = gradeDTO.comment.orEmpty()
            }
        }
    }

    fun getGrades(personId: Int, eventId: Int): List<PersonDemoGradeResponseRemote> {
        return transaction {
            select {
                Demo_grade.personId eq personId and (Demo_grade.eventId eq eventId)
            }.toList().map {
                PersonDemoGradeResponseRemote(
                    it[teamId].value,
                    it[level],
                    it[grade],
                    it[presentation],
                    it[additional],
                    it[comment],
                )
            }
        }
    }

    fun getAverage(eventId: Int, teamId: Int): StatisticsResponseRemote {
        return transaction {
            slice(level.avg(), grade.avg(), presentation.avg(), additional.avg())
                .select {Demo_grade.eventId eq eventId and (Demo_grade.teamId eq teamId)}
                .single().run {
                    StatisticsResponseRemote(
                        avgLevel = (this[level.avg()] ?: 0).toFloat(),
                        avgGrade = (this[grade.avg()] ?: 0).toFloat(),
                        avgPresentation = (this[presentation.avg()] ?: 0).toFloat(),
                        avgAdditional = (this[additional.avg()] ?: 0).toFloat()
                    )
                }
        }
    }

    fun getComments(eventId: Int, teamId: Int): List<CommentReceiveRemote> {
        return transaction {
            Join(Demo_grade,
                User,
                onColumn = personId,
                otherColumn = User.id)
                .slice(User.first_name, User.last_name, User.father_name, comment)
                .select { Demo_grade.eventId eq eventId and (Demo_grade.teamId eq teamId) }
                .map {
                    CommentReceiveRemote(
                        firstName = it[User.first_name],
                        lastName = it[User.last_name],
                        fatherName = it[User.father_name],
                        comment = it[comment]
                    )
                }
        }
    }
}
