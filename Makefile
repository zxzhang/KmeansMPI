proc_num = 5
cluster_num = 4
point_num = 25
dna_length = 30
max_value = 5
thet_point = 0.05
thet_dna = 1

compile:
	rm -rf ./bin
	mkdir ./bin
	mpijavac -d bin ./src/*/*.java

generate:
	python data/randomclustergen/generaterawdna.py  -c $(cluster_num) -p $(point_num) -o data/input/dna.txt -d $(dna_length)
	python data/randomclustergen/generaterawdata.py  -c $(cluster_num) -p $(point_num) -o data/input/data.txt -v $(max_value)

runpointp:
	mpirun -np $(proc_num) -machinefile machine.txt --mca btl_tcp_if_include eth0 java -cp ./bin datapoint.KmeansDataPointParallel data/input/data.txt $(cluster_num) $(thet_point)

runpoints:
	java -cp ./bin datapoint.KmeansDataPointSequential data/input/data.txt $(cluster_num) $(thet_point)

rundnap:
	mpirun -np $(proc_num) -machinefile machine.txt --mca btl_tcp_if_include eth0 java -cp ./bin dna.KmeansDnaParallel data/input/dna.txt $(cluster_num) $(thet_dna)

rundnas:
	java -cp ./bin dna.KmeansDnaSequential data/input/dna.txt $(cluster_num) $(thet_dna)

clean:
	rm -rf ./bin

