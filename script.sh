/usr/lib/jvm/java-1.11.0-openjdk-amd64/bin/java -javaagent:/home/cpsu2/ideaIC-2018.2.3/idea-IC-182.4323.46/lib/idea_rt.jar=44979:/home/cpsu2/ideaIC-2018.2.3/idea-IC-182.4323.46/bin -Dfile.encoding=UTF-8 -classpath "/home/cpsu2/IdeaProjects/TxScanner-v-2.0/out/production/TxScanner-v-2.0:/home/cpsu2/External jar/commons-codec-1.7.jar:/home/cpsu2/External jar/bitcoinj-core-0.14.7-bundled.jar:/home/cpsu2/External jar/apache-commons.jar:/home/cpsu2/External jar/commons-dbutils-1.7.jar" com.company.Main &

string=$(tail -1 "fileNo.properties");
id=$(echo $string | grep -Eo '[0-9]+$')


while [ $id -lt 97999999999 ]:
do
	echo "Pres CTRL+C to stop..."
	sleep 15 
	if (disaster-condition)
   	then
		break       	   #Abandon the loop.
   	fi

	string=$(tail -1 "fileNo.properties");
	id=$(echo $string | grep -Eo '[0-9]+$')

	echo "ID: $id"

	result=`expr "$id % 5" | bc`
	echo "Rem : $result"

	if [ 0 -eq  0 ]:
   	then
		echo "Processing New Batch with id: $id"
		
		echo "Killing JAVA"
		#sleep 60
		sudo killall java
			
		echo "Killed ALL JAVA"
		#sleep 8
		echo " Starting new instance for JAVA"
		/usr/lib/jvm/java-1.11.0-openjdk-amd64/bin/java -javaagent:/home/cpsu2/ideaIC-2018.2.3/idea-IC-182.4323.46/lib/idea_rt.jar=44979:/home/cpsu2/ideaIC-2018.2.3/idea-IC-182.4323.46/bin -Dfile.encoding=UTF-8 -classpath "/home/cpsu2/IdeaProjects/TxScanner-v-2.0/out/production/TxScanner-v-2.0:/home/cpsu2/External jar/commons-codec-1.7.jar:/home/cpsu2/External jar/bitcoinj-core-0.14.7-bundled.jar:/home/cpsu2/External jar/apache-commons.jar:/home/cpsu2/External jar/commons-dbutils-1.7.jar" com.company.Main &
		echo "Java Instance Created"
	fi
done
