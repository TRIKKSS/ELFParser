# Compiler les fonctions native java

## On génère le fichier de headers

A la suite de cette commande, on obtient un fichier header `.h`.

```
javac -h . CapstoneWrapper.java
```

## Code C

Ensuite on écris notre code C que l'on va venir compiler avec les librairies de java

```
gcc -I /usr/lib/jvm/java-21-openjdk/include  -I /usr/lib/jvm/java-21-openjdk/include/linux/ -lcapstone -fPIC -shared -o libmycapstone.so CapstoneWrapper.c
```

- `-I` sert à spécifier le chemin vers les libs java
- `-lcapstone` car on utilise capstone pour décompiler le code assembleur
- `-fPIC -shared` pour compiler une librairie partagée

## On peut ensuite executer notre code java

On passe en argument le chemin où se trouve la librairie qu'on vient de compiler.

```
java -Djava.library.path=$PWD CapstoneWrapper
```