#!/usr/bin/env bash

if [ -z "$T_HOME" ]; then
    T_HOME=/usr/local/tauris
fi
# Process name ( For display )
TAURIS=${T_HOME}/bin/tauris

SCRIPT=$0
SCRIPT_NAME=`basename $0`
BIN_DIR=`dirname ${SCRIPT}`
APP_HOME=$(cd ${BIN_DIR}/..; pwd)
BIN_DIR=${APP_HOME}/bin;
BASE_CONF=${APP_HOME}/config;
BASE_RUN=${APP_HOME}/run;
BASE_WORK=${APP_HOME}/work;
BASE_LOG=${APP_HOME}/logs;
APP_NAME=`echo $SCRIPT_NAME | cut -d . -f 1`
PRELOAD_DIR=${BIN_DIR}/${APP_NAME}.d

export T_HOME=$T_HOME
export TAURIS_APP=$APP_NAME

if [[ ! -d ${BASE_LOG} ]]; then
    mkdir ${BASE_LOG}
fi

if [[ ! -d ${BASE_WORK} ]]; then
    mkdir ${BASE_WORK}
fi

if [[ ! -d ${BASE_RUN} ]]; then
    mkdir ${BASE_RUN}
fi


function log_failure_msg() {
    echo "$@" "[ FAILED ]"
}

function log_success_msg() {
    echo "$@" "[ OK ]"
}

function checkproc() {
    pid=$1
    if [ "$pid" == "" ]; then
        if [ -f $PID_FILE ]; then
            pid=`cat $PID_FILE`
        else
            return 1
        fi
    fi
    ps -fp $pid |grep $APP_NAME > /dev/null
    return $?
}

PID_FILE=${BASE_RUN}/${APP_NAME}.pid;

CONFIG_FILENAME=${APP_NAME}.conf
CONFIG_FILEPATH=
if [ -f "${APP_HOME}/conf/$CONFIG_FILENAME" ]; then
    CONFIG_FILEPATH=${APP_HOME}/conf/$CONFIG_FILENAME
else
    CONFIG_FILEPATH=${APP_HOME}/config/$CONFIG_FILENAME
fi

cd $APP_HOME
case $1 in
    start)
        (checkproc)
        if [ $? == 0 ]; then
            log_failure_msg "$APP_NAME Process is running"
            exit 1
        fi
        if [ -d ${PRELOAD_DIR} ]; then
            for entry in `ls $PRELOAD_DIR`; do
                ex=${PRELOAD_DIR}/$entry
                if [ -x "$ex" ]; then
                    $ex ${APP_HOME}
                fi
            done
        fi
        if [ ! -f "$CONFIG_FILEPATH" ]; then
            log_failure_msg "config file not found"
            exit 1
        fi
        nohup $TAURIS $CONFIG_FILEPATH >${BASE_LOG}/$APP_NAME.out 2>${BASE_LOG}/$APP_NAME.err &
        pid=$!;
        sleep 3
        (checkproc $pid)
        if [ $? -eq 0 ]; then
            log_success_msg "$APP_NAME start success"
            echo $pid > $PID_FILE
            exit 0
        else
            log_failure_msg "$APP_NAME start failed"
            exit 1
        fi
        ;;
    stop)
        # Stop the daemon.
        echo  $SCRIPT_NAME stopping
        (checkproc)
        if [ $? -eq 0 ]; then
            pid=`cat $PID_FILE`
            kill $pid
            i=0
            while [ $i -lt 30 ]; do
                sleep 1
                (checkproc)
                if [ $? -ne 0 ]; then
                    break
                fi
                i=$[$i+1]
            done
            (checkproc)
            if [ $? -eq 0 ]; then
                log_failure_msg "$APP_NAME stop failed"
                exit 1
            else
                log_success_msg "$APP_NAME stop success"
                rm -rf $PID_FILE
                exit 0
            fi
        fi
        ;;

    force-stop)
        # Stop the daemon.
        (checkproc)
        if [ $? -eq 0 ]; then
            pid=`cat $PID_FILE`
            kill -9 $pid
            log_success_msg "$APP_NAME force stop success"
            exit 0
        fi
        ;;

    restart)
        # Restart the daemon.
        $APP_HOME/bin/$SCRIPT_NAME stop 
        i=0
        while [ $i -lt 30 ]; do
            sleep 1
            (checkproc)
            if [ $? -ne 0 ]; then
                break
            fi
            i=$[$i+1]
        done
        if [ $i -eq 30 ]; then
            log_failure_msg "$SCRIPT_NAME stop failed"
            exit 1
        fi
        $APP_HOME/bin/$SCRIPT_NAME start
        ;;

    reload)
        # reload config
        if [ ! -f $PID_FILE ]; then
            log_failure_msg "$APP_NAME is not running"
            exit 1
        fi
        pid=`cat $PID_FILE`
        kill -USR2 $pid
        ;;
    configtest)
        if [ ! -f "$CONFIG_FILEPATH" ]; then
            log_failure_msg "config file not found"
            exit 1
        fi
        # test config
        $TAURIS -t -f $CONFIG_FILEPATH
        ;;
    status)
        # Check the status of the process.
        (checkproc)
        if [ $? == 0 ]; then
            log_success_msg "$APP_NAME Process is running"
            exit 0
        else
            log_failure_msg "$APP_NAME Process is not running"
            exit 1
        fi
        ;;

    *)
        # For invalid arguments, print the usage message.
        echo "Usage: $0 {start|stop|force-stop|restart|reload|configtest|status}"
        exit 2
        ;;
esac
