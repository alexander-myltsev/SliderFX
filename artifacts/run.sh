#!/bin/sh
jarsigner -keystore coursegui_jnlp_keys cousegui.jar jdc
#jarsigner -keystore coursegui_jnlp_keys jmf.jar jdc
#jarsigner -keystore coursegui_jnlp_keys mail.jar jdc
#jarsigner -keystore coursegui_jnlp_keys miglayout-4.0-swing.jar jdc
#jarsigner -keystore coursegui_jnlp_keys scala-compiler.jar jdc
#jarsigner -keystore coursegui_jnlp_keys scala-library.jar jdc

cp cousegui.jar /c/Users/alex/Downloads/apache-tomcat-6.0.35/webapps/ROOT/
rm /c/Users/alex/AppData/LocalLow/Sun/Java/Deployment/log/*
javaws /c/Users/alex/Downloads/CourseAPC.jnlp