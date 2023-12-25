package com.ne_rabotaem.database.grade

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.person_team.PersonTeam
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.database.user.rank
import com.ne_rabotaem.features.demo.GradeResponseRemote
import com.ne_rabotaem.features.demo.statistics.CommentReceiveRemote
import com.ne_rabotaem.features.demo.statistics.StatisticsResponseRemote
import com.ne_rabotaem.features.vote.PersonDemoGradeResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Union
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object DemoGrade : IntIdTable("Demo_grade") {
    private val eventId = reference("event_id", Event.id)
    private val personId = reference("person_id", User.id)
    private val teamId = reference("team_id", Team.id)
    private val level = integer("level")
    private val grade = integer("grade")
    private val presentation = integer("presentation")
    private val additional = integer("additional")
    private val comment = varchar("comment", 500)
    private val imagePath = "/img/user_images/"

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
            DemoGrade.select { DemoGrade.eventId.eq(eventId) }.toList().map {
                GradeResponseRemote(
                    it[DemoGrade.id].value,
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
            val query = DemoGrade.select {
                DemoGrade.eventId.eq(eventId) and DemoGrade.personId.eq(userId) and DemoGrade.teamId.eq(teamId)
            }
            if (query.none())
                return@transaction null

            query.single().run {
                this[DemoGrade.id].value
            }
        }
    }

    fun update(gradeId: Int, gradeDTO: GradeDTO) {
        transaction {
            update( { DemoGrade.id eq gradeId } ) {
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
                DemoGrade.personId eq personId and (DemoGrade.eventId eq eventId)
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
            Join(DemoGrade,
                PersonTeam,
                joinType =  JoinType.FULL,
                onColumn = personId,
                otherColumn = PersonTeam.personId)
                .join(User,
                    joinType = JoinType.INNER,
                    onColumn = personId,
                    otherColumn = User.id)
                .slice(level.count(), level.avg(), grade.avg(), presentation.avg(), additional.avg())
                .select { DemoGrade.eventId eq eventId and
                        (DemoGrade.teamId eq teamId) and
                        (DemoGrade.teamId neq PersonTeam.teamId and
                                (PersonTeam.teamId neq null) or (User.rank_ eq rank.teacher)) }
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

    fun getAverage(eventId: Int): Map<Int, StatisticsResponseRemote> {
        return transaction {
            Join(DemoGrade,
                PersonTeam,
                joinType =  JoinType.FULL,
                onColumn = personId,
                otherColumn = PersonTeam.id)
                .join(User,
                    joinType = JoinType.INNER,
                    onColumn = personId,
                    otherColumn = User.id)
                .slice(teamId, level.avg(), grade.avg(), presentation.avg(), additional.avg())
                .select { DemoGrade.eventId eq eventId and
                        (teamId neq PersonTeam.teamId and
                                (PersonTeam.teamId neq null) or (User.rank_ eq rank.teacher)) }
                .groupBy(teamId)
                .associate {
                    it[teamId].value to
                            StatisticsResponseRemote(
                                avgLevel = (it[level.avg()] ?: 0).toFloat(),
                                avgGrade = (it[grade.avg()] ?: 0).toFloat(),
                                avgPresentation = (it[presentation.avg()] ?: 0).toFloat(),
                                avgAdditional = (it[additional.avg()] ?: 0).toFloat()
                            )
                }
        }
    }

    fun getComments(eventId: Int, teamId: Int): List<CommentReceiveRemote> {
        return transaction {
            Join(DemoGrade,
                User,
                onColumn = personId,
                otherColumn = User.id)
                .slice(User.first_name, User.last_name, User.father_name, comment, User.image_src)
                .select { DemoGrade.eventId eq eventId and (DemoGrade.teamId eq teamId) }
                .filter { it[comment].isNotEmpty() }
                .map {
                    CommentReceiveRemote(
                        firstName = it[User.first_name],
                        lastName = it[User.last_name],
                        fatherName = it[User.father_name],
                        comment = it[comment],
                        imageSrc = imagePath + (it[User.image_src] ?: "default.jpg")
                    )
                }
        }
    }

    fun getComments(eventId: Int): Map<Int, List<CommentReceiveRemote>> {
        val res = mutableMapOf<Int, MutableList<CommentReceiveRemote>>()

        transaction {
            Join(DemoGrade,
                User,
                onColumn = personId,
                otherColumn = User.id)
                .slice(teamId, User.first_name, User.last_name, User.father_name, comment, User.image_src)
                .select { DemoGrade.eventId eq eventId }
                .filter { it[comment].isNotEmpty() }
                .forEach {
                    if (!res.containsKey(it[teamId].value)) {
                        res[it[teamId].value] = mutableListOf()
                    }

                    res[it[teamId].value]!!.add(
                        CommentReceiveRemote(
                            firstName = it[User.first_name],
                            lastName = it[User.last_name],
                            fatherName = it[User.father_name],
                            comment = it[comment],
                            imageSrc = it[User.image_src] ?: "/img/user_images/default.jpg"
                        )
                    )
                }
        }

        return res
    }
}
