// Signature pour l'application

abstract sig User {
    username: one String,
    password: one String
}


sig CSVData {
  data: set Data
}

sig Data {}

sig OrowanModel {}

sig HMI {
  frictionCurve: FrictionCurve,
  rollSpeedCurve: RollSpeedCurve,
  sigmaCurve: SigmaCurve,
  password: lone String
}

sig Worker extends User{}

sig ProcessEngineer extends User{}

sig FrictionCurve {}

sig RollSpeedCurve {}

sig SigmaCurve {}

// Relations entre les différentes parties de l'application

sig System {
  storage: CSVData,
  model: OrowanModel,
  hmi: set HMI,
  accessControl: set User,
  processManagement: set Worker + ProcessEngineer,
  equipmentControl: set FrictionCurve + RollSpeedCurve + SigmaCurve
}

// Contraintes du système

fact SystemConstraints {
  #System.storage = 1 // il ne doit y avoir qu'un seul stockage de données
  #System.model = 1 // il ne doit y avoir qu'un seul modèle Orowan
}

// Contraintes sur la base de données

fact CSVDataConstraints {
  CSVData.data = Data // la base de données ne doit contenir que des données
}

// Contraintes sur les courbes

fact CurveConstraints {
  FrictionCurve + RollSpeedCurve + SigmaCurve = System.equipmentControl // toutes les courbes doivent être incluses dans l'équipement de contrôle
}

// Contraintes sur les utilisateurs

fact UserConstraints {
  System.accessControl in System.processManagement // tous les utilisateurs doivent être associés à la gestion des processus
}

// Contraintes sur les processus

fact ProcessConstraints {
  all w: Worker | w in System.processManagement // tous les travailleurs doivent être inclus dans la gestion des processus
  all pe: ProcessEngineer | pe in System.processManagement // tous les ingénieurs de processus doivent être inclus dans la gestion des processus
}

// Contraintes sur l'interface homme-machine

fact HMIConstraints {
  all h: HMI | h in System.hmi // toutes les interfaces homme-machine doivent être incluses dans le système
  all h: HMI | h.frictionCurve in System.equipmentControl // toutes les courbes de friction doivent être incluses dans l'équipement de contrôle
  all h: HMI | h.rollSpeedCurve in System.equipmentControl // toutes les courbes de vitesse de rotation doivent être incluses dans l'équipement de contrôle
  all h: HMI | h.sigmaCurve in System.equipmentControl // toutes les courbes Sigma doivent être incluses dans l'équipement de contrôle
  one h: HMI | lone h.password // il ne peut y avoir qu'un mot de passe unique pour l'ensemble des interfaces homme-machine
}

// Contraintes sur le modèle Orowan

fact OrowanConstraints {
  System.model in System.processManagement // le modèle Orowan doit être inclus dans la gestion des processus
}

// Contraintes sur l'équipement de contrôle

fact EquipmentControlConstraints {
  all fc: FrictionCurve | fc in System.equipmentControl // toutes les courbes de friction doivent être incluses dans l'équipement de contrôle
  all rsc: RollSpeedCurve | rsc in System.equipmentControl // toutes les courbes de vitesse de rotation doivent être incluses dans l'équipement de contrôle
  all sc: SigmaCurve | sc in System.equipmentControl
}

sig TimeSeriesDatabase {}

// Contraintes sur la base de données en série temporelle

fact TimeSeriesDatabaseConstraints {
System.storage in TimeSeriesDatabase // la base de données doit être incluse dans la série temporelle
System.model in TimeSeriesDatabase // le modèle Orowan doit être incluse dans la série temporelle
all fc: FrictionCurve | fc in TimeSeriesDatabase // toutes les courbes de friction doivent être incluses dans la série temporelle
all rsc: RollSpeedCurve | rsc in TimeSeriesDatabase // toutes les courbes de vitesse de rotation doivent être incluses dans la série temporelle
all sc: SigmaCurve | sc in TimeSeriesDatabase // toutes les courbes Sigma doivent être incluses dans la série temporelle
}


// Contraintes pour la protection par mot de passe

fact PasswordProtectionConstraints {
all u: User | u.password != none // tous les utilisateurs doivent avoir un mot de passe
}

// Contraintes pour l'utilisation de GRPC

sig LII {}

fact GRPCConstraints {
System.model in LII // le modèle doit être inclus dans le simulateur LII basé sur gRPC
}

// Contraintes pour l'affichage des données

fact DisplayConstraints {
all data: Data | data in TimeSeriesDatabase // toutes les données doivent être incluses dans la base de données en série temporelle
all fc: FrictionCurve | fc.data in TimeSeriesDatabase // toutes les données de friction doivent être incluses dans la base de données en série temporelle
all rsc: RollSpeedCurve | rsc.data in TimeSeriesDatabase // toutes les données de vitesse de rotation doivent être incluses dans la base de données en série temporelle
all sc: SigmaCurve | sc.data in TimeSeriesDatabase // toutes les données Sigma doivent être incluses dans la base de données en série temporelle
}
