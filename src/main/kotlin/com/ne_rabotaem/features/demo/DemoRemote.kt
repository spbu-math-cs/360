package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.EventType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.xml.stream.events.Comment

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), DateTimeFormatter.BASIC_ISO_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }
}

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), DateTimeFormatter.BASIC_ISO_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class EventResponseRemote(
    val eventId: Int,
    val type: EventType,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    @Serializable(with = LocalTimeSerializer::class)
    val start: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val finish: LocalTime
)

@Serializable
data class TeamResponseRemote(
    val teamId: Int,
    val number: Int,
    val name: String,
    val projectName: String,
    val teacherId: Int
)

@Serializable
data class GradeReceiveRemote(
    val eventId: Int,
    val teamId: Int,
    val level: Int,
    val grade: Int,
    val presentation: Int,
    val additional: Int,
    val comment: String
)

@Serializable
data class GradeResponseRemote(
    val gradeId: Int,
    val personId: Int,
    val teamId: Int,
    val level: Int,
    val grade: Int,
    val presentation: Int,
    val additional: Int,
    val comment: String
)

@Serializable
data class StatisticsResponseRemote(
    val avgLevel: Float,
    val avgGrade: Float,
    val avgPresentation: Float,
    val avgAdditional: Float
)

@Serializable
data class StatisticsReceiveRemote(
    val teamId: Int,
    val eventId: Int
)

@Serializable
data class CommentReceiveRemote(
    val firstName: String,
    val lastName: String,
    val fatherName: String,
    val comment: String
)