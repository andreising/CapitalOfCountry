package com.andreising.capitalofcountry.capital.domain

abstract class DomainException : IllegalStateException()

class NoInternetConnectionException() : DomainException()

class IllegalCapitalException() : DomainException()

class ServiceUnavailableException() : DomainException()