#!/usr/bin/sh

PROGNAME=`basename "$0"`
BASEDIR=$(dirname $0)
SCRIPT_PATH="$0"
APP_NAME="Snap"
# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

if [ x = x"$SNAP_HOME" ]
then
   SNAP_HOME="$BASEDIR"
fi  

warn ( ) {
    echo "${PROGNAME}: $*"
}

die ( ) {
    warn "$*"
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;; 
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
esac

# Attempt to set JAVA_HOME if it's not already set.
if [ -z "$JAVA_HOME" ] ; then
    if $darwin ; then 
        [ -z "$JAVA_HOME" -a -f "/usr/libexec/java_home" ] && export JAVA_HOME=`/usr/libexec/java_home`
        [ -z "$JAVA_HOME" -a -d "/Library/Java/Home" ] && export JAVA_HOME="/Library/Java/Home"
        [ -z "$JAVA_HOME" -a -d "/System/Library/Frameworks/JavaVM.framework/Home" ] && export JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Home"
    else
        javaExecutable="`which javac`"
        [ -z "$javaExecutable" -o "`expr \"$javaExecutable\" : '\([^ ]*\)'`" = "no" ] && die "JAVA_HOME not set and cannot find javac to deduce location, please set JAVA_HOME."
        # readlink(1) is not available as standard on Solaris 10.
        readLink=`which readlink`
        [ `expr "$readLink" : '\([^ ]*\)'` = "no" ] && die "JAVA_HOME not set and readlink not available, please set JAVA_HOME."
        javaExecutable="`readlink -f \"$javaExecutable\"`"
        javaHome="`dirname \"$javaExecutable\"`"
        javaHome=`expr "$javaHome" : '\(.*\)/bin'`
        JAVA_HOME="$javaHome"
        export JAVA_HOME

    fi
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched.
if $cygwin ; then
    [ -n "$SNAP_HOME" ] && SNAP_HOME=`cygpath --unix "$SNAP_HOME"`
    [ -n "$JAVACMD" ] && JAVACMD=`cygpath --unix "$JAVACMD"`
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
else
    if [ -n "$SNAP_HOME" -a `expr "$SNAP_HOME":'\/$'` ] ; then
        SNAP_HOME=`echo $SNAP_HOME | sed -e 's/\/$//'`
    fi
fi

#  For MSYS, ensure paths are in appropriate format.
if $msys
then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`( cd "$JAVA_HOME" ; pwd )`
fi

# Attempt to set SNAP_HOME if it is not already set.
if [ -z "$SNAP_HOME" -o ! -d "$SNAP_HOME" ] ; then
    # Resolve links: $0 may be a link to groovy's home.
    PRG="$0"
    # Need this for relative symlinks.
    while [ -h "$PRG" ] ; do
        ls=`ls -ld "$PRG"`
        link=`expr "$ls" : '.*-> \(.*\)$'`
        if expr "$link" : '/.*' > /dev/null; then
            PRG="$link"
        else
            PRG=`dirname "$PRG"`"/$link"
        fi
    done
    SAVED="`pwd`"
    cd "`dirname \"$PRG\"`/.."
    SNAP_HOME="`pwd -P`"
    cd "$SAVED"
fi

# Determine the Java command to use to start the JVM.
if [ -z "$JAVACMD" ] ; then
    if [ -n "$JAVA_HOME" ] ; then
        if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
            # IBM's JDK on AIX uses strange locations for the executables
            JAVACMD="$JAVA_HOME/jre/sh/java"
        else
            JAVACMD="$JAVA_HOME/bin/java"
        fi
    else
        JAVACMD="java"
    fi
fi
if [ ! -x "$JAVACMD" ] ; then
    die "JAVA_HOME is not defined correctly, can not execute: $JAVACMD"
fi
if [ -z "$JAVA_HOME" ] ; then
    warn "JAVA_HOME environment variable is not set"
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query businessSystem maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Setup Profiler
useprofiler=false
if [ "$PROFILER" != "" ] ; then
    if [ -r "$PROFILER" ] ; then
        . $PROFILER
        useprofiler=true
    else
        die "Profiler file not found: $PROFILER"
    fi
fi

# For Darwin, add APP_NAME to the JAVA_OPTS as -Xdock:name
if $darwin; then
    JAVA_OPTS="$JAVA_OPTS -Xdock:name=$APP_NAME"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    SNAP_HOME=`cygpath --mixed "$SNAP_HOME"`
    JAVA_HOME=`cygpath --mixed "$JAVA_HOME"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$SNAP_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN="$OURCYGPATTERN|($SNAP_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        if [ $CHECK -ne 0 ] ; then
            patched=`cygpath --path --ignore --mixed "$arg"`
        else
            patched="$arg"
        fi
        if [ x"$BASH" = x ]; then
            eval `echo args$i`="\"$arg\""
        else
            args[$i]="$patched"
        fi
        i=`expr $i + 1`
    done

    if [ x"$BASH" = x ]; then
        case $i in
            0) set -- ;;
            1) set -- "$args0" ;;
            2) set -- "$args0" "$args1" ;;
            3) set -- "$args0" "$args1" "$args2" ;;
            4) set -- "$args0" "$args1" "$args2" "$args3" ;;
            5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
            6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
            7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
            8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
            9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
        esac
    else
        set -- "${args[@]}"
    fi
fi

exec "$JAVACMD" $JAVA_OPTS -jar $SNAP_HOME/snap.jar "--directory=." "--verbose=false" "--script=$1" $2 $3 $4 $5 $6 $7 $8 $9
