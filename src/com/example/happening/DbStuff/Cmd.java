package com.example.happening.DbStuff;

import java.io.Serializable;

public enum Cmd implements Serializable{
    ADD_HAPPENING_TO_DB, GET_HAPPENINGS, DELETE_ATTEND, ADD_ATTEND, ADD_COMMENT, GET_COMMENTS, GET_ATTENDERS;
}
