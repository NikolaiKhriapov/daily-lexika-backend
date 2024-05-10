alter table reviews
    alter column date_last_completed type timestamp with time zone
        using date_last_completed at time zone 'UTC',
    alter column  date_generated type timestamp with time zone
        using date_generated at time zone 'UTC';

alter table words
    alter column date_of_last_occurrence type timestamp with time zone
        using date_of_last_occurrence at time zone 'UTC';

alter table role_statistics
    alter column date_of_last_streak type timestamp with time zone
        using date_of_last_streak at time zone 'UTC';

alter table notifications
    alter column sent_at type timestamp with time zone
        using sent_at at time zone 'UTC';
