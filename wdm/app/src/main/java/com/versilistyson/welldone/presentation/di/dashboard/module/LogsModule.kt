package com.versilistyson.welldone.presentation.di.dashboard.module

import com.versilistyson.welldone.data.datasource.log.LogLocalDataSourceImpl
import com.versilistyson.welldone.data.datasource.log.LogRemoteDataSourceImpl
import com.versilistyson.welldone.data.repository.LogRepositoryImpl
import com.versilistyson.welldone.domain.framework.datasource.log.LogLocalDataSource
import com.versilistyson.welldone.domain.framework.datasource.log.LogRemoteDataSource
import com.versilistyson.welldone.domain.framework.repository.LogRepository
import com.versilistyson.welldone.domain.framework.usecases.log.GetLogsUseCase
import com.versilistyson.welldone.presentation.di.dashboard.DashboardActivityScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class LogsModule {

    @DashboardActivityScope
    @Binds
    abstract fun bindLogLocalDataSource(dataSource: LogLocalDataSourceImpl): LogLocalDataSource

    @DashboardActivityScope
    @Binds
    abstract fun bindLogRemoteDataSource(dataSource: LogRemoteDataSourceImpl): LogRemoteDataSource

    @DashboardActivityScope
    @Binds
    abstract fun bindLogRepository(logRepository: LogRepositoryImpl): LogRepository
}