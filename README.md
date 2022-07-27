# its just a hopper

It's just a hopper. Thanks to unascribed and TwilightFlower for doing the toolchain heavy lifting and for providing an example[.](https://github.com/BuildCraft/BuildCraft/tree/3.4.x)

### hey quat you use windows, but una says the script only supports linux. How did you build this

Could try to convert it to a batch file but didn't feel like it. "Git bash" also doesn't have `zip`, which is used on the last line, and it's hard to add packaged on git bash... I mean I could do that zip step manually but why do it the easy way when!!!!!!

* get WSL debian (i think i got debian stretch (9)).
* You need specifically Java 11 in the VM to use gradle (just a limitation of this project), so follow directions in [this mf](https://linuxize.com/post/install-java-on-debian-9/)
  * add the stretch-backports repo: `echo 'deb http://ftp.debian.org/debian stretch-backports main' | sudo tee /etc/apt/sources.list.d/stretch-backports.list`
  * `sudo apt update` and `sudo apt install openjdk-11-jdk`. Tada
* Get file compression utilities required by the script into the VM: `sudo apt install zip unzip`
* Haha wow bizarre "trustAnchors parameter must be non-empty" error when invoking `./gradlew` from the container, awesome, [go here](https://gist.github.com/mikaelhg/527204e746984cf9a33f7910bb8b4cb6)
* Nooooww you should be able to run `./gradlew` from the guest OS and it will set up the files you need.

What you probably should do instead: (because the gradle script is smart about not trying to run the shell script if the file already exists)

* Just `sudo apt install zip unzip` in the container, it's all you need.
* On the Windows host, run the gradle until it breaks from not being able to execute the shell script.
* On the Linux guest, run the shell script.
* On the Windows host, run the gradle again, it should work now.

Goofy ahh shit that happens:

* On the original project (buildcraft), the gradle looks for the property `project.minecraft_version` but i don't think one was defined anywhere. I fixed it.
* `mcp726a.zip` kept downloading as a 0-byte file, not sure why (maybe a 404). You can browse the archive and download it yourself [here](https://ia601701.us.archive.org/view_archive.php?archive=/29/items/minecraftcoderpack/minecraftcoderpack.zip).
* The shell script breaks if a `mcp726a-forged.zip` file already exists. Delete the file before running the shell script.
* For some reason Voldeloom downloads version_manifest.json as a gzip file (in `~/.gradle/caches/fabric-loom/version_manifest.json`), doesn't actually unzip it, then blows up with json parsing error when trying to read it.
  * Had a bunch of text about "here use this shell script to inflate the files" but it turned out to be a deeper problem with Voldeloom so I just PRd it.

Yes run configs don't work, I mean voldeloom prints warnings about "fabric-installer.json not found in classpath", what did you expect would happen