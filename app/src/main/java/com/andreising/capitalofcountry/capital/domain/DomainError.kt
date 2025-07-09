package com.andreising.capitalofcountry.capital.domain

abstract class DomainError : IllegalStateException()

class NoInternetConnection : DomainError()

class IllegalCapitalName : DomainError()

class ServiceUnavailable : DomainError()