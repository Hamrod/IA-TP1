# Compte rendu TP Dames IA

**Barbot Malo
Beurel Luca**

IMPORTANT : Nous vous avons rendu le TP par mail dans le temps imparti avant que nous nous rendions compte qu'il fallait le rendre par moodle. Désolé du temps de retard affiché sur le rendu.

Après plusieurs essais sur différentes valeurs de **c** nous avons constaté que $\sqrt{2}$ était une valeur plutot équilibrée, nous l'avons donc choisie.

Nous avons fait quelques statistiques pour tenter de mesurer l'efficacité de notre IA.
Sur 10 runs : 
- *Blanc Random Vs Noir MCTS 10s* => 6 victoires du joueur Noir, 3 égalités et 1 victoire joueur du blanc.
- *Blanc MCTS 10s Vs Noir Random* => 3 victoires du joueur Noir, 2 égalités et 5 victoires joueur du blanc.

Autre jeux de tests : 
- *Blanc MCTS 2s Vs Noir MCTS 10s* => Victoire systématique du joueur Noir.
- *Blanc MCTS 10s Vs Noir MCTS 2s* => Victoire systématique du joueur Noir.

Nous avons donc très probablement un biais dans notre recherche car les noirs sont beaucoup trop avantagé par rapport aux blancs. Même si le jeu de base a tendance à favoriser ce coté les résultats sont trop marqués pour que cela ne soit dû qu'au jeu.

Nous avons aussi fait un autre test avec le MCTS 10s contre un autre MCTS 10, la partie se solve par la victoire du joueur noir en 45 tours. A chaque test de cette configuration nous obtenons ce résultat ce qui est cohérent puisque notre IA va systématiquement prendre le même chemin d'exploration.


Nous n'avons pas eu de choix d'implémentation crucial à faire, nous avons juste rajouté un paramètre sur **uct()** pour pouvoir accéder au nombre de fois où le parent a été visité.
