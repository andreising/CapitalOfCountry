package com.andreising.capitalofcountry.capital.domain

abstract class DomainException : IllegalStateException()

class NoInternetConnectionException() : DomainException()

class IllegalCapitalName() : DomainException()

class ServiceUnavailableException() : DomainException()