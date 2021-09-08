package com.example.pisid2021.APP.Database;

import android.provider.BaseColumns;

public class DatabaseConfig {

    public static class Medicao implements BaseColumns {
        public static final String TABLE_NAME="medicao";
        public static final String COLUMN_NAME_ID_MEDICAO ="ID_Medicao";
        public static final String COLUMN_NAME_ID_ZONA ="ID_Zona";
        public static final String COLUMN_NAME_TIPO = "Tipo";
        public static final String COLUMN_NAME_HORA ="Hora";
        public static final String COLUMN_NAME_LEITURA ="Leitura";
        public static final String COLUMN_NAME_OUTLIER ="Outlier";
    }

    public static class Alerta implements BaseColumns {
        public static final String TABLE_NAME="alerta";
        public static final String COLUMN_NAME_ID_ALERTA ="ID_Alerta";
        public static final String COLUMN_NAME_HORA ="Hora";
        public static final String COLUMN_NAME_TIPO_ALERTA ="Tipo_Alerta";
        public static final String COLUMN_NAME_MENSAGEM ="Mensagem";
        public static final String COLUMN_NAME_ID_UTILIZADOR ="ID_Utilizador";
        public static final String COLUMN_NAME_ID_CULTURA ="ID_Cultura";
        public static final String COLUMN_NAME_ID_MEDICAO ="ID_Medicao";
    }

    protected static final String SQL_CREATE_MEDICAO =
            "CREATE TABLE " + Medicao.TABLE_NAME +
                    " (" + Medicao.COLUMN_NAME_ID_MEDICAO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Medicao.COLUMN_NAME_HORA + " TIMESTAMP," +
                    Medicao.COLUMN_NAME_TIPO + " TEXT," +
                    Medicao.COLUMN_NAME_LEITURA + " DOUBLE," +
                    Medicao.COLUMN_NAME_OUTLIER + " BOOLEAN," +
                    Medicao.COLUMN_NAME_ID_ZONA + " TEXT )";

    protected static final String SQL_DELETE_MEDICAO_DATA =
            "DELETE FROM " + Medicao.TABLE_NAME;

    protected static final String SQL_CREATE_ALERTA =
            "CREATE TABLE " + Alerta.TABLE_NAME +
                    " (" + Alerta.COLUMN_NAME_ID_ALERTA + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Alerta.COLUMN_NAME_HORA + " TIMESTAMP," +
                    Alerta.COLUMN_NAME_TIPO_ALERTA + " INTEGER," +
                    Alerta.COLUMN_NAME_MENSAGEM + " TEXT," +
                    Alerta.COLUMN_NAME_ID_UTILIZADOR + " INTEGER," +
                    Alerta.COLUMN_NAME_ID_CULTURA + " INTEGER," +
                    Alerta.COLUMN_NAME_ID_MEDICAO + " INTEGER )";

    protected static final String SQL_DELETE_ALERTA_DATA =
            "DELETE FROM " + Alerta.TABLE_NAME;

    protected static final String SQL_CREATE_DROP_ALERTA_IFEXISTS=("DROP TABLE IF EXISTS ") + Alerta.TABLE_NAME;
    protected static final String SQL_CREATE_DROP_MEDICAO_IFEXISTS =("DROP TABLE IF EXISTS ") + Medicao.TABLE_NAME;

}
