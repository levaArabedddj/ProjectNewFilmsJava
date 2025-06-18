package com.example.RabbitMQ.DtoRabbitMQ;

import com.example.Entity.Castings;
import com.example.Entity.TrialParticipants;
import com.example.Entity.Users;
import com.example.Enum.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastingApplicationAnswerEvent {

    private String filmName;
    private Long castingId;
    private String actorName;
    private String actorGmail;
    private String status;
}
