proc_num = 5
cluster_num = 4
point_num = 25
dna_length = 30
max_value = 5

compile:
	rm -rf ./bin
	mkdir ./bin
	mpijavac -d bin ./src/*/*.java

generate:
	python data/randomclustergen/generaterawdna.py  -c $(cluster_num) -p $(point_num) -o data/input/dna.txt -d $(dna_length)
	python data/randomclustergen/generaterawdata.py  -c $(cluster_num) -p $(point_num) -o data/input/data.txt -v $(max_value)

runpointp:
	mpirun -np $(proc_num) -machinefile machine.txt --mca btl_tcp_if_include eth0 java -cp ./bin datapoint.KmeansDataPointParallel data/input/data.txt $(cluster_num)

runpoints:
	java -cp ./bin datapoint.KmeansDataPointSequential data/input/data.txt $(cluster_num)

rundnap:
	mpirun -np $(proc_num) -machinefile machine.txt --mca btl_tcp_if_include eth0 java -cp ./bin dna.KmeansDnaParallel data/input/dna.txt $(cluster_num)

rundnas:
	java -cp ./bin dna.KmeansDnaSequential data/input/dna.txt $(cluster_num)

clean:
	rm -rf ./bin

