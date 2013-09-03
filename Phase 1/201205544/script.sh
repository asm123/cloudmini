#!/bin/bash

pm_file=$1
image_file=$2
types_file=$3

#read image file and store image names in an array
echo "Reading image_file..."
i=0
while read line
do
	line=`echo "$line" | sed -e 's/^ *//g' -e 's/ *$//g'`
	case "$line" in
		"") continue;;
	esac
	image[$i]=$line
	i=$i+1
#	echo "Image: $line"
done < "$image_file"

echo -e "Copying image files from all the physical machines...\n"
dir="/home/my_images/"
if [ -d "$dir" ]; then
	echo "Directory: $dir exists. Deleting..."
	rm -rf "$dir"
fi
mkdir "$dir"
echo "Directory $dir created!"
image_names_file="/home/image_names"
for image in "${image[@]}"
do
	echo "Copying $image to my $dir ..."
	scp $image $dir
	echo "Copied!"
	echo $image | sed 's/.*:\(.*\)\/\(.*\)/\2/' >> $image_names_file
done

while read host
do
        host=`echo "$host" | sed -e 's/^ *//g' -e 's/ *$//g'`
        case "$host" in
                "") continue;;
        esac
	
	dir="/home/my_images"
	remote_dir="$host:/home"
	echo "Copying $dir to $remote_dir..."
	scp -r $dir $remote_dir
	echo "Copied to $remote_dir"
done < "$pm_file"

java -jar bin/cloud_orch.jar $pm_file $image_names_file $types_file
